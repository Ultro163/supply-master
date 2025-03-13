package com.example.supplymaster.dto.shipment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO для запроса на добавление товара в отгрузку.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на добавление товара в отгрузку")
public class ShipmentItemRequest {
    @NotNull
    @Schema(description = "Идентификатор продукта", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID productId;
    @NotNull
    @Positive
    @Schema(description = "Вес товара в килограммах", example = "25.5")
    private BigDecimal weightKg;
}