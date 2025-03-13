package com.example.supplymaster.dto.shipment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO для представления отчета об отгрузке в ответе сервиса.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Отчет об отгрузке")
public class ResponseShipmentReportDto {
    @Schema(description = "Идентификатор поставщика", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID supplierId;
    @Schema(description = "Название поставщика", example = "'Сады Кубани'")
    private String supplierName;
    @Schema(description = "Список отгруженных товаров")
    private List<ResponseShipmentItemDto> shipments;
}