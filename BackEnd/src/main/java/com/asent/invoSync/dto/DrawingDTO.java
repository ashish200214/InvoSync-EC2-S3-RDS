package com.asent.invoSync.dto;
import lombok.Data;
import java.util.*;

@Data
public class DrawingDTO {
    private Long id;
    private String drawingUrl;
    private Long quotationId;
    private Long billId;
    private List<MediaDTO> media;
}
