package com.asent.invoSync.dto;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class S3FileInfo {
    private String key;
    private String fileName;
    private String url;
    private String contentType;
    private Long size;
    private Instant lastModified; // ISO instant
}
