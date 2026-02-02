package com.asent.invoSync.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Media {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imagesUrl;
    private String pdfUrl;

    @ManyToOne
    @JoinColumn(name = "drawing_id")
    private Drawing drawing;
}
