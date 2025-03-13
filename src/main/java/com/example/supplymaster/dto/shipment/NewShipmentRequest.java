package com.example.supplymaster.dto.shipment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO для запроса на создание новой отгрузки.
 * Содержит дату отгрузки и список товаров в отгрузке.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на создание новой отгрузки")
public class NewShipmentRequest {
    @NotNull
    @Schema(description = "Дата отгрузки", example = "2024-06-01")
    private LocalDate shipmentDate;
    @NotNull
    @Schema(description = "Список товаров в отгрузке")
    private List<ShipmentItemRequest> shipmentItems = new ArrayList<>();
}