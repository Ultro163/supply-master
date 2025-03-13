package com.example.supplymaster.controller;

import com.example.supplymaster.dto.mappers.ShipmentMapper;
import com.example.supplymaster.dto.shipment.NewShipmentRequest;
import com.example.supplymaster.dto.shipment.ResponseShipmentReportDto;
import com.example.supplymaster.dto.shipment.UpdateShipmentRequest;
import com.example.supplymaster.service.shipment.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления поставками.
 * <p>
 * Позволяет создавать, обновлять и получать отчеты о поставках.
 */
@Tag(name = "Поставки", description = "API для управления поставками и отчетами о них")
@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentService shipmentService;
    private final ShipmentMapper shipmentMapper;

    /**
     * Создает новую поставку.
     *
     * @param newShipmentRequest объект с данными о новой поставке.
     * @param supplierId         идентификатор поставщика, передается в заголовке запроса.
     * @return UUID созданной поставки.
     */
    @Operation(summary = "Создать поставку", description = "Создает новую поставку для указанного поставщика.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Поставка успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "403", description = "Отказано в доступе")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createShipment(@RequestBody @Valid NewShipmentRequest newShipmentRequest,
                               @RequestHeader UUID supplierId) {
        return shipmentService.createShipment(supplierId, newShipmentRequest.getShipmentDate(),
                newShipmentRequest.getShipmentItems());
    }

    /**
     * Обновляет существующую поставку.
     *
     * @param updateShipmentRequest объект с новыми данными для поставки.
     * @param supplierId            идентификатор поставщика, передается в заголовке запроса.
     * @param shipmentId            идентификатор обновляемой поставки.
     * @return UUID обновленной поставки.
     */
    @Operation(summary = "Обновить поставку", description = "Обновляет существующую поставку по ее идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Поставка успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Поставка не найдена")
    })
    @PatchMapping("/{shipmentId}")
    public UUID updateShipment(@RequestBody UpdateShipmentRequest updateShipmentRequest,
                               @RequestHeader UUID supplierId,
                               @PathVariable UUID shipmentId) {
        return shipmentService.updateShipment(shipmentId, supplierId, updateShipmentRequest.getShipmentDate(),
                updateShipmentRequest.getShipmentItems());
    }

    /**
     * Получает отчет о поставках за заданный период.
     *
     * @param startDate начальная дата периода.
     * @param endDate   конечная дата периода.
     * @return список DTO с отчетными данными о поставках.
     */
    @Operation(summary = "Получить отчет о поставках", description = "Возвращает отчет о поставках за указанный период.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отчет успешно получен"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @GetMapping("/report")
    public List<ResponseShipmentReportDto> getShipmentsReport(@RequestParam LocalDate startDate,
                                                              @RequestParam LocalDate endDate) {
        return shipmentService.getShipmentsReport(startDate, endDate).stream()
                .map(shipmentMapper::toResponseShipmentReportDto).toList();
    }
}