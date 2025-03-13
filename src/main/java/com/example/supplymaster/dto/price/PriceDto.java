package com.example.supplymaster.dto.price;

import com.example.supplymaster.dto.product.ProductDto;
import com.example.supplymaster.dto.supplier.SupplierDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) для представления информации о цене.
 * Используется для передачи информации между слоями приложения.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceDto {
    private UUID id;
    private SupplierDto supplier;
    private ProductDto product;
    private BigDecimal pricePerKg;
    private LocalDate startDate;
    private LocalDate endDate;
}