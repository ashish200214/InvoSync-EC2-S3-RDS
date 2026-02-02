package com.asent.invoSync.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="drawing_id")
    private Drawing drawing;

    // NEW: link back to the quotation (one-to-one)
    @OneToOne
    @JoinColumn(name = "quotation_id")
    private Quotation quotation;

    // NEW: pdf url stored after upload
    private String pdfUrl;

    private Double total;
    private Double remainingAmount;

    @OneToMany(mappedBy = "billing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();
}
