package com.example.supplymaster.dto.mappers;

import com.example.supplymaster.dto.shipment.ResponseShipmentReportDto;
import com.example.supplymaster.dto.shipment.ShipmentItemReportGraphDto;
import com.example.supplymaster.dto.shipment.ShipmentReportDto;
import com.example.supplymaster.entity.ShipmentItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Маппер для преобразования объектов, связанный с сущностью Shipment.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShipmentMapper {
    ResponseShipmentReportDto toResponseShipmentReportDto(ShipmentReportDto shipmentReportDto);

    @Mapping(source = "product.id", target = "productId")
    ShipmentItemReportGraphDto toGraphDto(ShipmentItem shipmentItem);
}