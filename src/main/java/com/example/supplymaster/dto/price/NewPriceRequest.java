package com.example.supplymaster.dto.price;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO для запроса на создание новой цены.
 * Содержит информацию о поставщике, продукте, цене и периоде действия.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на создание новой цены для продукта")
public class NewPriceRequest {
    @NotNull
    @Schema(description = "Идентификатор поставщика", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID supplierId;
    @NotNull
    @Schema(description = "Идентификатор продукта", example = "110e8400-e29b-41d4-a716-446655440000")
    private UUID productId;
    @NotNull
    @Positive
    @Schema(description = "Цена за килограмм", example = "25.50")
    private BigDecimal pricePerKg;
    @NotNull
    @Future
    @Schema(description = "Дата начала действия цены (должна быть в будущем)", example = "2025-06-01")
    private LocalDate startDate;
    @NotNull
    @Future
    @Schema(description = "Дата окончания действия цены (должна быть в будущем)", example = "2025-12-31")
    private LocalDate endDate;
}