package com.asent.invoSync.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "drawing_id")
    private Drawing drawing;

    private LocalDateTime date;
    private Double total;
    private String status;
    private String initialRequirement;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();
}
