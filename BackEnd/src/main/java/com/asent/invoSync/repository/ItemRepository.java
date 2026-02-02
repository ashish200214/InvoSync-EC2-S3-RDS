package com.asent.invoSync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.asent.invoSync.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
