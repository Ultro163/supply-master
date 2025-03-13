package com.example.supplymaster.dto.shipment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO для представления информации о товаре в отгрузке в ответе сервиса.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Информация о товаре в отгрузке")
public class ResponseShipmentItemDto {
    @Schema(description = "Идентификатор продукта", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID productId;
    @Schema(description = "Название продукта", example = "Яблоки Антоновка")
    private String productName;
    @Schema(description = "Общий вес товара в килограммах", example = "150.75")
    private BigDecimal totalWeightKg;
    @Schema(description = "Общая стоимость товара", example = "12000.50")
    private BigDecimal totalPrice;
}