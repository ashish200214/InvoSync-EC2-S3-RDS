package com.asent.invoSync.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuotationDTO {
    private Long id;
    private CustomerDTO customer;
    private Long drawingId;
    private LocalDateTime date;
    private Double total;
    private String status;
    private List<ItemDTO> items;
    private String initialRequirement;

    // convenience flat fields for form submissions (optional)
    private String name;
    private String email;
    private String whatsAppNo;
}
