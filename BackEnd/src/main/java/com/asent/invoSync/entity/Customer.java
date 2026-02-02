package com.asent.invoSync.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String whatsAppNo;
    private String email;
    private LocalDateTime date;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quotation> quotations = new ArrayList<>();
}
