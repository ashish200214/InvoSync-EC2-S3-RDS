package com.asent.invoSync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asent.invoSync.entity.Drawing;

public interface DrawingRepository extends JpaRepository<Drawing, Long> {}
