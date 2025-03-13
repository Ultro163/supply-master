package com.example.supplymaster.dto.price;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO для запроса на обновление цены.
 * Содержит новую цену за килограмм продукта.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на обновление цены за килограмм продукта")
public class UpdatePriceRequest {
    @NotNull
    @Positive
    @Schema(description = "Новая цена за килограмм", example = "12.50")
    private BigDecimal pricePerKg;
}