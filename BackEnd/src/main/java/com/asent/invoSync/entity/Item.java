package com.asent.invoSync.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String particular;
    private Integer quantity;
    private Double price;
    private Double total;

    @ManyToOne
    @JoinColumn(name = "quotation_id")
    private Quotation quotation;

    @ManyToOne
    @JoinColumn(name = "billing_id")
    private Bill billing;
}
