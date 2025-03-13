package com.example.supplymaster.service.price;

import com.example.supplymaster.dto.mappers.PriceMapper;
import com.example.supplymaster.dto.price.PriceDto;
import com.example.supplymaster.entity.Price;
import com.example.supplymaster.error.exception.EntityNotFoundException;
import com.example.supplymaster.error.exception.PriceConflictException;
import com.example.supplymaster.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Реализация сервиса для управления ценами на продукты.
 */
@RequiredArgsConstructor
@Service
@Transactional
public class PriceServiceImpl implements PriceService {
    private final PriceRepository priceRepository;
    private final PriceMapper priceMapper;

    /**
     * Создает новую цену для продукта, проверяя отсутствие пересечения дат.
     *
     * @param priceDto DTO с информацией о цене.
     * @return UUID созданной записи цены.
     * @throws PriceConflictException если цена на этот период уже существует.
     */
    @Override
    public UUID createPriceForProduct(PriceDto priceDto) {
        boolean existsPrice = priceRepository.checkIsNoIntersectionTime(
                priceDto.getSupplier().getId(),
                priceDto.getProduct().getId(),
                priceDto.getStartDate(),
                priceDto.getEndDate());

        if (!existsPrice) {
            throw new PriceConflictException("Ошибка: Цена на этот период уже существует!");
        }

        Price newPrice = priceMapper.toEntity(priceDto);
        return priceRepository.save(newPrice).getId();
    }

    /**
     * Обновляет существующую цену для продукта.
     *
     * @param priceDto DTO с новой информацией о цене.
     * @return UUID обновленной записи цены.
     * @throws EntityNotFoundException если цена не найдена.
     */
    @Override
    public UUID updatePriceForProduct(PriceDto priceDto) {
        Price existsPrice = priceRepository.findById(priceDto.getId()).orElseThrow(() ->
                new EntityNotFoundException("Цена для данного поставщика и продукта не найдена."));
        existsPrice.setPricePerKg(priceDto.getPricePerKg());
        return existsPrice.getId();
    }
}