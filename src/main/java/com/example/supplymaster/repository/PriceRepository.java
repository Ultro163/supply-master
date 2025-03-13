package com.example.supplymaster.repository;

import com.example.supplymaster.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link Price}.
 */
@Repository
public interface PriceRepository extends JpaRepository<Price, UUID> {

    /**
     * Проверяет, существует ли пересечение ценовых диапазонов для заданного поставщика и продукта.
     *
     * @param supplierId ID поставщика.
     * @param productId  ID продукта.
     * @param startDate  Дата начала нового ценового диапазона.
     * @param endDate    Дата окончания нового ценового диапазона.
     * @return {@code true}, если пересечений нет, иначе {@code false}.
     */
    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN false ELSE true END
            FROM Price p
            WHERE p.supplier.id = :supplierId
              AND p.product.id = :productId
              AND (:startDate BETWEEN p.startDate AND p.endDate
                OR :endDate BETWEEN p.startDate AND p.endDate
                OR p.startDate BETWEEN :startDate AND :endDate)
            """)
    boolean checkIsNoIntersectionTime(@Param("supplierId") UUID supplierId,
                                      @Param("productId") UUID productId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    /**
     * Находит актуальные цены для списка продуктов поставщика на указанную дату отгрузки.
     *
     * @param supplierId   ID поставщика.
     * @param productIds   Список ID продуктов.
     * @param shipmentDate Дата отгрузки.
     * @return Список цен, действующих на указанную дату отгрузки.
     */
    @Query("""
            SELECT p
            FROM Price p
            WHERE p.supplier.id = :supplierId
              AND p.product.id IN :productId
              AND :shipmentDate BETWEEN p.startDate AND p.endDate
            """)
    List<Price> findPriceForSupplierProducts(@Param("supplierId") UUID supplierId,
                                             @Param("productId") Set<UUID> productIds,
                                             @Param("shipmentDate") LocalDate shipmentDate);
}