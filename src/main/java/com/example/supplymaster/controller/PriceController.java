package com.example.supplymaster.controller;

import com.example.supplymaster.dto.mappers.PriceMapper;
import com.example.supplymaster.dto.price.NewPriceRequest;
import com.example.supplymaster.dto.price.PriceDto;
import com.example.supplymaster.dto.price.UpdatePriceRequest;
import com.example.supplymaster.service.price.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Контроллер для управления ценами на продукты.
 * <p>
 * Позволяет создавать и обновлять цены.
 */
@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
@Tag(name = "Цены", description = "API для управления ценами продуктов")
public class PriceController {
    private final PriceService priceService;
    private final PriceMapper priceMapper;

    /**
     * Создает новую цену для продукта.
     *
     * @param newPriceRequest объект с данными о новой цене.
     * @return UUID созданной цены.
     */
    @Operation(summary = "Создать цену", description = "Создает новую цену для продукта и возвращает его идентификатор.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Цена успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "409", description = "Конфликт цен")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createPriceForProduct(@RequestBody @Valid NewPriceRequest newPriceRequest) {
        PriceDto priceDto = priceMapper.toPriceDtoFromNewPriceRequest(newPriceRequest);
        return priceService.createPriceForProduct(priceDto);
    }

    /**
     * Обновляет существующую цену продукта.
     *
     * @param priceId            идентификатор обновляемой цены.
     * @param updatePriceRequest объект с новыми данными для цены.
     * @return UUID обновленной цены.
     */
    @Operation(summary = "Обновить цену", description = "Обновляет существующую цену продукта по его идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Цена успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Цена не найдена")
    })
    @PatchMapping("/{priceId}")
    public UUID updatePriceForProduct(@PathVariable UUID priceId,
                                      @RequestBody @Valid UpdatePriceRequest updatePriceRequest) {
        PriceDto priceDto = priceMapper.toPriceDtoFromUpdatePriceRequest(updatePriceRequest);
        priceDto.setId(priceId);
        return priceService.updatePriceForProduct(priceDto);
    }
}