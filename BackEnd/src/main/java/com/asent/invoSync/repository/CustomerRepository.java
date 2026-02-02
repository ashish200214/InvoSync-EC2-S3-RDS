package com.asent.invoSync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.asent.invoSync.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByWhatsAppNo(String whatsAppNo);
}
