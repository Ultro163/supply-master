package com.example.supplymaster.dto.shipment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO для представления отчета по отгрузке.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentReportDto {
    private UUID supplierId;
    private String supplierName;
    private List<ShipmentItemDto> shipments;
}