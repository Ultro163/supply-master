package com.example.supplymaster.dto.shipment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO для представления информации о товаре в отгрузке.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentItemDto {
    private UUID supplierId;
    private String supplierName;
    private UUID productId;
    private String productName;
    private BigDecimal totalWeightKg;
    private BigDecimal totalPrice;
}