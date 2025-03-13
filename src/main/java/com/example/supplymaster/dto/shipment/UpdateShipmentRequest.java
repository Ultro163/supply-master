package com.example.supplymaster.dto.shipment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO для запроса на обновление информации об отгрузке.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на обновление информации об отгрузке")
public class UpdateShipmentRequest {
    @Schema(description = "Новая дата отгрузки", example = "2025-04-15")
    private LocalDate shipmentDate;
    @Schema(description = "Список товаров в обновляемой отгрузке")
    private List<ShipmentItemRequest> shipmentItems = new ArrayList<>();
}