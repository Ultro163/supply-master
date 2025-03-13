package com.example.supplymaster.service.shipment;

import com.example.supplymaster.dto.shipment.ShipmentItemReportGraphDto;
import com.example.supplymaster.dto.shipment.ShipmentItemRequest;
import com.example.supplymaster.dto.shipment.ShipmentReportDto;
import com.example.supplymaster.entity.Price;
import com.example.supplymaster.entity.Product;
import com.example.supplymaster.entity.Shipment;
import com.example.supplymaster.entity.ShipmentItem;
import com.example.supplymaster.entity.Supplier;
import com.example.supplymaster.error.exception.AccessDeniedException;
import com.example.supplymaster.error.exception.EntityNotFoundException;
import com.example.supplymaster.repository.PriceRepository;
import com.example.supplymaster.repository.ProductRepository;
import com.example.supplymaster.repository.ShipmentRepository;
import com.example.supplymaster.repository.SupplierRepository;
import com.example.supplymaster.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ShipmentServiceImplIT {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void cleanDb() {
        shipmentRepository.deleteAll();
        supplierRepository.deleteAll();
        productRepository.deleteAll();
        priceRepository.deleteAll();
    }

    private void createTestPrice(Supplier supplier, Product product) {
        Price price = new Price();
        price.setSupplier(supplier);
        price.setProduct(product);
        price.setPricePerKg(new BigDecimal("100.00"));
        price.setStartDate(LocalDate.now());
        price.setEndDate(LocalDate.now().plusMonths(1));
        priceRepository.save(price);
    }

    @Test
    void createShipment_shouldSaveShipment() {
        Supplier supplier = TestUtil.createTestSupplier();
        Product product = TestUtil.createTestProduct();

        createTestPrice(supplier, product);

        ShipmentItemRequest itemRequest = new ShipmentItemRequest();
        itemRequest.setProductId(product.getId());
        itemRequest.setWeightKg(new BigDecimal("10"));

        UUID shipmentId = shipmentService.createShipment(supplier.getId(), LocalDate.now(), List.of(itemRequest));

        assertNotNull(shipmentId);

        Shipment savedShipment = shipmentRepository.findFetchById(shipmentId).orElse(null);
        assertNotNull(savedShipment);
        assertNotNull(savedShipment.getShipmentItems());
        assertEquals(1, savedShipment.getShipmentItems().size());
    }

    @Test
    void updateShipment_shouldUpdateShipment() {
        Supplier supplier = TestUtil.createTestSupplier();
        Product product = TestUtil.createTestProduct();

        createTestPrice(supplier, product);

        ShipmentItemRequest itemRequest = new ShipmentItemRequest();
        itemRequest.setProductId(product.getId());
        itemRequest.setWeightKg(new BigDecimal("10"));

        UUID shipmentId = shipmentService.createShipment(supplier.getId(), LocalDate.now(), List.of(itemRequest));

        ShipmentItemRequest updatedItemRequest = new ShipmentItemRequest();
        updatedItemRequest.setProductId(product.getId());
        updatedItemRequest.setWeightKg(new BigDecimal("20"));

        UUID updatedShipmentId = shipmentService.updateShipment(shipmentId, supplier.getId(), LocalDate.now(), List.of(updatedItemRequest));

        assertNotNull(updatedShipmentId);

        Shipment updatedShipment = shipmentRepository.findFetchById(updatedShipmentId).orElse(null);
        assertNotNull(updatedShipment);
        List<ShipmentItem> expectedShipmentItems = updatedShipment.getShipmentItems().stream().toList();
        assertEquals(new BigDecimal("30.00"), expectedShipmentItems.getFirst().getWeightKg());
    }

    @Test
    void getShipmentsReport_shouldReturnShipmentsReport() {
        Supplier supplier = TestUtil.createTestSupplier();
        Product product = TestUtil.createTestProduct();

        createTestPrice(supplier, product);

        ShipmentItemRequest itemRequest = new ShipmentItemRequest();
        itemRequest.setProductId(product.getId());
        itemRequest.setWeightKg(new BigDecimal("10"));

        shipmentService.createShipment(supplier.getId(), LocalDate.now(), List.of(itemRequest));

        List<ShipmentReportDto> report = shipmentService.getShipmentsReport(
                LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(1));
        assertNotNull(report);
        assertFalse(report.isEmpty());
    }

    @Test
    void getShipmentItemsReports_shouldReturnShipmentItems() {
        Supplier supplier = TestUtil.createTestSupplier();
        Product product = TestUtil.createTestProduct();

        createTestPrice(supplier, product);

        ShipmentItemRequest itemRequest = new ShipmentItemRequest();
        itemRequest.setProductId(product.getId());
        itemRequest.setWeightKg(new BigDecimal("10"));

        UUID shipmentId = shipmentService.createShipment(supplier.getId(), LocalDate.now(), List.of(itemRequest));

        List<ShipmentItemReportGraphDto> itemReports = shipmentService.getShipmentItemsReports(shipmentId);

        assertNotNull(itemReports);
        assertFalse(itemReports.isEmpty());
    }

    @Test
    void updateShipment_shouldThrowEntityNotFoundException() {
        ShipmentItemRequest itemRequest = new ShipmentItemRequest();
        itemRequest.setProductId(UUID.randomUUID());
        itemRequest.setWeightKg(new BigDecimal("10"));

        Executable executable = () -> shipmentService.updateShipment(UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now(),
                List.of(itemRequest));

        assertThrows(EntityNotFoundException.class, executable);
    }

    @Test
    void updateShipment_shouldThrowAccessDeniedException() {
        Supplier supplier1 = TestUtil.createTestSupplier();
        Supplier supplier2 = TestUtil.createTestSupplier();
        Product product = TestUtil.createTestProduct();

        createTestPrice(supplier1, product);

        ShipmentItemRequest itemRequest = new ShipmentItemRequest();
        itemRequest.setProductId(product.getId());
        itemRequest.setWeightKg(new BigDecimal("10"));

        UUID shipmentId = shipmentService.createShipment(supplier1.getId(), LocalDate.now(), List.of(itemRequest));

        Executable executable = () -> shipmentService.updateShipment(shipmentId,
                supplier2.getId(),
                LocalDate.now(),
                List.of(itemRequest));

        assertThrows(AccessDeniedException.class, executable);
    }
}