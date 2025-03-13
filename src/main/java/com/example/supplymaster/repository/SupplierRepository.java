package com.example.supplymaster.repository;

import com.example.supplymaster.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link Supplier}.
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
}