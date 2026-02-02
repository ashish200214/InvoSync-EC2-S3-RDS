package com.asent.invoSync.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.asent.invoSync.dto.S3FileInfo;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class S3Service {

    @Value("${aws.accessKeyId}") private String accessKey;
    @Value("${aws.secretAccessKey}") private String secretKey;
    @Value("${aws.s3.bucketName}") private String bucketName;
    @Value("${aws.region}") private String region;

    private S3Client client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    private S3Presigner presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    /**
     * Upload file into: <customerWhats>/<subFolder>/<baseName><ext>
     * Returns public or presigned URL depending on ACL & bucket policy
     */
    public String uploadFileWithCustomerFolder(MultipartFile file, String customerWhatsApp, String subFolder, String baseName) throws IOException {
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) ext = original.substring(original.lastIndexOf("."));
        String key = (customerWhatsApp == null || customerWhatsApp.isBlank() ? "unknown" : customerWhatsApp)
                + "/" + subFolder + "/" + baseName + ext;

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ) // optional: if you want public access
                .contentType(file.getContentType())
                .build();

        client().putObject(putReq, RequestBody.fromBytes(file.getBytes()));

        // Return public URL (works if public-read)
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }

    /**
     * List files for a customer prefix.
     *
     * @param whatsApp customer folder prefix (eg. '919876543210')
     * @param presign  if true generate presigned GET urls (expiry in seconds)
     * @param presignExpirySeconds expiry for presigned url
     */
    public List<S3FileInfo> listFilesForCustomer(String whatsApp, boolean presign, long presignExpirySeconds) {
        String prefix = (whatsApp == null || whatsApp.isBlank()) ? "" : whatsApp + "/";

        ListObjectsV2Request req = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        ListObjectsV2Response resp = client().listObjectsV2(req);

        List<S3Object> objects = resp.contents();
        if (objects == null || objects.isEmpty()) return Collections.emptyList();

        List<S3FileInfo> result = new ArrayList<>();
        for (S3Object obj : objects) {
            String key = obj.key();
            String fileName = key.contains("/") ? key.substring(key.lastIndexOf("/") + 1) : key;
            String url;

            if (!presign) {
                url = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
            } else {
                try (S3Presigner p = presigner()) {
                    GetObjectRequest getReq = GetObjectRequest.builder().bucket(bucketName).key(key).build();
                    GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofSeconds(Math.max(60, presignExpirySeconds)))
                            .getObjectRequest(getReq)
                            .build();
                    url = p.presignGetObject(presignReq).url().toString();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // fallback to public URL (may 403)
                    url = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
                }
            }

            // Optionally get contentType via HEAD (slower) - omitted for speed
            S3FileInfo info = new S3FileInfo();
            info.setKey(key);
            info.setFileName(fileName);
            info.setUrl(url);
            info.setSize(obj.size());
            Instant lm = obj.lastModified(); // SDK v2 returns Instant
            info.setLastModified(lm);
            result.add(info);
        }
        return result;
    }
}
