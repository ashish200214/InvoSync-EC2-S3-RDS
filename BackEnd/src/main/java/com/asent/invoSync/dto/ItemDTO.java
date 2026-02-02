package com.asent.invoSync.dto;

import lombok.*;

@Data
public class ItemDTO {
    private Long id;
    private Integer quantity;
    private String particular;
    private Double price;
    private Double total;
    private Long quotationId;
    private Long billingId;
}
