package com.asent.invoSync.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import com.asent.invoSync.entity.Quotation;
import java.util.Optional;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    // fetch items and customer to avoid lazy issues
    @Query("SELECT q FROM Quotation q LEFT JOIN FETCH q.items LEFT JOIN FETCH q.customer WHERE q.id = :id")
    Optional<Quotation> findByIdWithItems(@Param("id") Long id);
}
