package com.example.supplymaster.repository;

import com.example.supplymaster.entity.ShipmentItem;
import com.example.supplymaster.entity.ShipmentItemKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link ShipmentItem}.
 */
@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, ShipmentItemKey> {
    /**
     * Получает список всех элементов отгрузки по идентификатору отгрузки.
     *
     * @param shipmentId идентификатор отгрузки
     * @return список элементов отгрузки
     */
    List<ShipmentItem> findAllByShipmentId(UUID shipmentId);
}