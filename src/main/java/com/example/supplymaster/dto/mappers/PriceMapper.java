package com.example.supplymaster.dto.mappers;

import com.example.supplymaster.dto.price.NewPriceRequest;
import com.example.supplymaster.dto.price.PriceDto;
import com.example.supplymaster.dto.price.UpdatePriceRequest;
import com.example.supplymaster.entity.Price;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Маппер для преобразования объектов, связанных с сущностью Price.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PriceMapper {
    Price toEntity(PriceDto priceDto);

    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "supplierId", target = "supplier.id")
    PriceDto toPriceDtoFromNewPriceRequest(NewPriceRequest newPriceRequest);

    PriceDto toPriceDtoFromUpdatePriceRequest(UpdatePriceRequest updatePriceRequest);
}