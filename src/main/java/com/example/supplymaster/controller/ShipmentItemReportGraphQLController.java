package com.example.supplymaster.controller;

import com.example.supplymaster.dto.shipment.ShipmentItemReportGraphDto;
import com.example.supplymaster.service.shipment.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

/**
 * GraphQL-контроллер для получения данных о товарных позициях в поставке.
 * <p>
 * Позволяет запрашивать отчет о поставке через GraphQL.
 */
@Controller
@RequiredArgsConstructor
public class ShipmentItemReportGraphQLController {
    private final ShipmentService shipmentService;

    /**
     * Получает отчет о товарных позициях в конкретной поставке.
     *
     * @param shipmentId идентификатор поставки.
     * @return список DTO с отчетными данными о товарах в поставке.
     */
    @QueryMapping
    public List<ShipmentItemReportGraphDto> getShipmentItemsReports(@Argument UUID shipmentId) {
        return shipmentService.getShipmentItemsReports(shipmentId);
    }
}