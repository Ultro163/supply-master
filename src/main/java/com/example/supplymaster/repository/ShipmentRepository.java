package com.example.supplymaster.repository;

import com.example.supplymaster.dto.shipment.ShipmentItemDto;
import com.example.supplymaster.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link Shipment}.
 */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

    /**
     * Получает сводную информацию об отгрузках товаров за указанный период.
     *
     * @param startDate начальная дата периода
     * @param endDate   конечная дата периода
     * @return список DTO {@link ShipmentItemDto}, содержащих информацию о поставщиках, товарах, общем весе и стоимости
     */
    @Query("""
                SELECT new com.example.supplymaster.dto.shipment.ShipmentItemDto(
                    sup.id,
                    sup.name,
                    p.id,
                    p.name,
                    SUM(si.weightKg),
                    SUM(si.totalPrice)
                )
                FROM ShipmentItem si
                JOIN si.shipment s
                JOIN s.supplier sup
                JOIN si.product p
                WHERE s.shipmentDate BETWEEN :startDate AND :endDate
                GROUP BY sup.id, sup.name, p.id, p.name
            """)
    List<ShipmentItemDto> getProductShipmentSummary(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    /**
     * Находит отгрузку по идентификатору с немедленной загрузкой связанных элементов отгрузки.
     *
     * @param shipmentId идентификатор отгрузки
     * @return опциональный объект {@link Shipment}, если найден
     */
    @Query("""
            select s
            from Shipment s
            join fetch s.shipmentItems
            where s.id = :shipmentId
            """)
    Optional<Shipment> findFetchById(@Param("shipmentId") UUID shipmentId);
}