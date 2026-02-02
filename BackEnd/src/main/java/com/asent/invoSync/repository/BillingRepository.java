package com.asent.invoSync.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import com.asent.invoSync.entity.Bill;
import java.util.List;
import java.util.Optional;

public interface BillingRepository extends JpaRepository<Bill, Long> {

    @Query("select distinct b from Bill b left join fetch b.items it left join fetch b.quotation q left join fetch q.customer where b.id = :id")
    Optional<Bill> findByIdWithItemsAndQuotationAndCustomer(@Param("id") Long id);

    @Query("select distinct b from Bill b left join fetch b.items it left join fetch b.quotation q left join fetch q.customer")
    List<Bill> findAllWithItemsAndQuotationAndCustomer();
}
