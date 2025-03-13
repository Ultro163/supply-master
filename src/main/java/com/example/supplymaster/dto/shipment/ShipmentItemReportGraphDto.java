package com.example.supplymaster.dto.shipment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO для представления данных о товаре по конкретной отгрузке в GraphQl.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentItemReportGraphDto {
    private UUID productId;
    private Float weightKg;
    private Float pricePerKg;
    private Float totalPrice;
}