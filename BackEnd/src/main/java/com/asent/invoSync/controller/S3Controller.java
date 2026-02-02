package com.asent.invoSync.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import java.util.List;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    @Autowired private com.asent.invoSync.service.S3Service s3Service;

    // GET /api/s3/files/{whatsApp}?presign=true&expiry=300
    @GetMapping("/files/{whatsApp}")
    public ResponseEntity<List<com.asent.invoSync.dto.S3FileInfo>> listFiles(
            @PathVariable String whatsApp,
            @RequestParam(name="presign", required=false, defaultValue = "false") boolean presign,
            @RequestParam(name="expiry", required=false, defaultValue = "300") long expirySeconds
    ){
        try {
            List<com.asent.invoSync.dto.S3FileInfo> files = s3Service.listFilesForCustomer(whatsApp, presign, expirySeconds);
            return ResponseEntity.ok(files);
        } catch(Exception ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

