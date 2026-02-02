package com.asent.invoSync.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Drawing {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;
    private String fileName;
}
