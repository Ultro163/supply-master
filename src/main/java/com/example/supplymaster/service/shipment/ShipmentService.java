package com.example.supplymaster.service.shipment;

import com.example.supplymaster.dto.shipment.ShipmentItemReportGraphDto;
import com.example.supplymaster.dto.shipment.ShipmentItemRequest;
import com.example.supplymaster.dto.shipment.ShipmentReportDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для управления отгрузками.
 */
public interface ShipmentService {

    UUID createShipment(UUID supplierId, LocalDate shipmentDate, List<ShipmentItemRequest> requestShipmentItems);

    UUID updateShipment(UUID shipmentId, UUID supplierId, LocalDate shipmentDate,
                        List<ShipmentItemRequest> requestShipmentItems);

    List<ShipmentReportDto> getShipmentsReport(LocalDate startDate, LocalDate endDate);

    List<ShipmentItemReportGraphDto> getShipmentItemsReports(UUID shipmentId);
}
