package com.example.supplymaster.repository;

import com.example.supplymaster.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link Product}.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
}