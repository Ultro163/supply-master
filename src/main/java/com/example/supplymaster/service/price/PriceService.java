package com.example.supplymaster.service.price;

import com.example.supplymaster.dto.price.PriceDto;

import java.util.UUID;

/**
 * Сервис для управления ценами на продукты.
 */
public interface PriceService {
    UUID createPriceForProduct(PriceDto priceDto);

    UUID updatePriceForProduct(PriceDto priceDto);
}