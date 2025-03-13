package com.example.supplymaster.service.shipment;

import com.example.supplymaster.dto.mappers.ShipmentMapper;
import com.example.supplymaster.dto.shipment.ShipmentItemDto;
import com.example.supplymaster.dto.shipment.ShipmentItemReportGraphDto;
import com.example.supplymaster.dto.shipment.ShipmentItemRequest;
import com.example.supplymaster.dto.shipment.ShipmentReportDto;
import com.example.supplymaster.entity.Price;
import com.example.supplymaster.entity.Product;
import com.example.supplymaster.entity.Shipment;
import com.example.supplymaster.entity.ShipmentItem;
import com.example.supplymaster.entity.ShipmentItemKey;
import com.example.supplymaster.entity.Supplier;
import com.example.supplymaster.error.exception.AccessDeniedException;
import com.example.supplymaster.error.exception.EntityNotFoundException;
import com.example.supplymaster.repository.PriceRepository;
import com.example.supplymaster.repository.ProductRepository;
import com.example.supplymaster.repository.ShipmentItemRepository;
import com.example.supplymaster.repository.ShipmentRepository;
import com.example.supplymaster.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для управления отгрузками.
 */
@RequiredArgsConstructor
@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final PriceRepository priceRepository;
    private final ProductRepository productRepository;
    private final ShipmentItemRepository shipmentItemRepository;
    private final SupplierRepository supplierRepository;
    private final ShipmentMapper shipmentMapper;

    /**
     * Создает новую отгрузку для указанного поставщика.
     *
     * @param supplierId           идентификатор поставщика.
     * @param shipmentDate         дата отгрузки.
     * @param requestShipmentItems список товаров в отгрузке.
     * @return UUID созданной отгрузки.
     */
    @Override
    public UUID createShipment(UUID supplierId, LocalDate shipmentDate, List<ShipmentItemRequest> requestShipmentItems) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new EntityNotFoundException("Поставщик не найден с ID " + supplierId));

        Shipment shipment = new Shipment();
        shipment.setSupplier(supplier);
        shipment.setShipmentDate(shipmentDate);

        Set<UUID> productIds = requestShipmentItems.stream()
                .map(ShipmentItemRequest::getProductId)
                .collect(Collectors.toSet());

        Map<UUID, Product> existProducts = getExistProducts(productIds);

        Map<UUID, Price> supplierProductPrices = getSupplierProductPrices(supplierId, productIds, shipmentDate);

        Set<ShipmentItem> shipmentItems = new HashSet<>();

        for (ShipmentItemRequest requestItem : requestShipmentItems) {
            UUID productId = requestItem.getProductId();
            Price price = supplierProductPrices.get(productId);
            Product product = existProducts.get(productId);
            validateProduct(product, price);

            ShipmentItem shipmentItem = createShipmentItem(shipment,
                    product, requestItem.getWeightKg(), price);

            shipmentItems.add(shipmentItem);
        }
        shipment.setShipmentItems(shipmentItems);

        return shipmentRepository.save(shipment).getId();
    }

    /**
     * Обновляет существующую отгрузку.
     *
     * @param shipmentId           идентификатор отгрузки.
     * @param supplierId           идентификатор поставщика.
     * @param shipmentDate         новая дата отгрузки.
     * @param requestShipmentItems обновленный список товаров в отгрузке.
     * @return UUID обновленной отгрузки.
     */
    @Override
    public UUID updateShipment(UUID shipmentId, UUID supplierId,
                               LocalDate shipmentDate, List<ShipmentItemRequest> requestShipmentItems) {
        Shipment existsShipment = shipmentRepository.findFetchById(shipmentId).orElseThrow(() ->
                new EntityNotFoundException("Поставка с ID " + shipmentId + " не найден в базе данных"));
        if (!existsShipment.getSupplier().getId().equals(supplierId)) {
            throw new AccessDeniedException("Вы не можете изменить эту поставку.");
        }
        Optional.ofNullable(shipmentDate).ifPresent(existsShipment::setShipmentDate);

        Set<UUID> allProductIds = new HashSet<>();
        requestShipmentItems.forEach(rsi -> allProductIds.add(rsi.getProductId()));
        existsShipment.getShipmentItems().forEach(si -> allProductIds.add(si.getProduct().getId()));

        Map<UUID, Product> existProducts = getExistProducts(allProductIds);

        Map<UUID, Price> supplierProductPrices = getSupplierProductPrices(supplierId, allProductIds,
                existsShipment.getShipmentDate());

        Map<UUID, ShipmentItem> shipmentProduct = existsShipment.getShipmentItems().stream()
                .collect(Collectors.toMap(existsShipmentItem -> existsShipmentItem.getProduct().getId(),
                        existsShipmentItem -> existsShipmentItem));

        for (ShipmentItemRequest requestItem : requestShipmentItems) {
            UUID productId = requestItem.getProductId();
            Product product = existProducts.get(productId);
            Price price = supplierProductPrices.get(productId);
            validateProduct(product, price);

            Optional.ofNullable(shipmentProduct.get(productId))
                    .ifPresentOrElse(
                            si -> {
                                si.setWeightKg(si.getWeightKg().add(requestItem.getWeightKg()));
                                si.setTotalPrice(si.getPricePerKg().multiply(si.getWeightKg()));
                            },
                            () -> {
                                ShipmentItem shipmentItem = createShipmentItem(existsShipment,
                                        product, requestItem.getWeightKg(), price);

                                existsShipment.getShipmentItems().add(shipmentItem);
                            }
                    );
        }
        return shipmentRepository.save(existsShipment).getId();

    }

    /**
     * Получает отчет по отгрузкам за заданный период.
     *
     * @param startDate начальная дата периода.
     * @param endDate   конечная дата периода.
     * @return список отчетов по отгрузкам.
     */
    @Transactional(readOnly = true)
    public List<ShipmentReportDto> getShipmentsReport(LocalDate startDate, LocalDate endDate) {
        List<ShipmentItemDto> productSummaries = shipmentRepository.getProductShipmentSummary(startDate, endDate);

        Map<UUID, List<ShipmentItemDto>> supplierShipments = productSummaries.stream()
                .collect(Collectors.groupingBy(ShipmentItemDto::getSupplierId));

        Map<UUID, Supplier> supplierMap = supplierRepository.findAllById(supplierShipments.keySet()).stream()
                .collect(Collectors.toMap(Supplier::getId, p -> p));

        return supplierShipments.entrySet().stream()
                .map(entry -> new ShipmentReportDto(
                        entry.getKey(),
                        supplierMap.get(entry.getKey()).getName(),
                        entry.getValue()
                ))
                .toList();
    }

    /**
     * Получает отчет о товарах в конкретной отгрузке.
     *
     * @param shipmentId идентификатор отгрузки.
     * @return список товаров в отгрузке с их характеристиками.
     */
    @Override
    public List<ShipmentItemReportGraphDto> getShipmentItemsReports(UUID shipmentId) {
        return shipmentItemRepository.findAllByShipmentId(shipmentId).stream()
                .map(shipmentMapper::toGraphDto).toList();
    }

    /**
     * Получает Map существующих продуктов по их идентификаторам.
     *
     * @param productIds список идентификаторов продуктов.
     * @return Map продуктов.
     */
    private Map<UUID, Product> getExistProducts(Set<UUID> productIds) {
        return productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
    }

    /**
     * Получает Map цен для продуктов поставщика на указанную дату.
     *
     * @param supplierId   идентификатор поставщика.
     * @param productIds   список идентификаторов продуктов.
     * @param shipmentDate дата отгрузки.
     * @return Map цен.
     */
    private Map<UUID, Price> getSupplierProductPrices(UUID supplierId, Set<UUID> productIds, LocalDate shipmentDate) {
        return priceRepository.findPriceForSupplierProducts(supplierId,
                        productIds, shipmentDate).stream()
                .collect(Collectors.toMap(price -> price.getProduct().getId(), p -> p));
    }

    /**
     * Проверяет, существует ли продукт и его цена.
     *
     * @param product продукт.
     * @param price   цена.
     */
    private void validateProduct(Product product, Price price) {
        if (product == null) {
            throw new EntityNotFoundException("Продукт не найден в базе данных");
        }

        if (price == null) {
            throw new EntityNotFoundException("Цена не найдена для продукта");
        }
    }

    /**
     * Создает новый объект {@link ShipmentItem}, представляющий товар в отгрузке.
     *
     * @param shipment Объект {@link Shipment}, к которому относится данный товар.
     * @param product  Объект {@link Product}, который включен в отгрузку.
     * @param weightKg Вес товара в килограммах.
     * @param price    Объект {@link Price}, содержащий информацию о цене за килограмм.
     * @return Новый экземпляр {@link ShipmentItem} с заданными параметрами.
     */
    private ShipmentItem createShipmentItem(Shipment shipment, Product product,
                                            BigDecimal weightKg, Price price) {
        ShipmentItem shipmentItem = new ShipmentItem();
        shipmentItem.setId(new ShipmentItemKey());
        shipmentItem.setShipment(shipment);
        shipmentItem.setProduct(product);
        shipmentItem.setWeightKg(weightKg);
        shipmentItem.setPricePerKg(price.getPricePerKg());
        shipmentItem.setTotalPrice(price.getPricePerKg().multiply(weightKg));
        return shipmentItem;
    }
}