package com.asent.invoSync.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingDTO {
    private Long id;
    private Long drawingId;
    private Double total;
    private Double remainingAmount;
    private List<ItemDTO> items;

    // keep customer object too (optional)
    private CustomerDTO customer;
    private String name; // fallback flat name

    // NEW fields used by your service
    private Long quotationId;
    private String pdfUrl;

    private Long customerId;
    private String customerName;
    private String customerEmail;
}
