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
import com.example.supplymaster.entity.Supplier;
import com.example.supplymaster.error.exception.AccessDeniedException;
import com.example.supplymaster.error.exception.EntityNotFoundException;
import com.example.supplymaster.repository.PriceRepository;
import com.example.supplymaster.repository.ProductRepository;
import com.example.supplymaster.repository.ShipmentItemRepository;
import com.example.supplymaster.repository.ShipmentRepository;
import com.example.supplymaster.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceImplTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ShipmentItemRepository shipmentItemRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ShipmentMapper shipmentMapper;

    @InjectMocks
    private ShipmentServiceImpl shipmentService;

    private UUID supplierId;
    private Supplier supplier;
    private Product product;
    private Price price;
    private Shipment shipment;
    private List<ShipmentItemRequest> shipmentItemRequests;

    @BeforeEach
    void setUp() {
        supplierId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        supplier = new Supplier();
        supplier.setId(supplierId);
        supplier.setName("Test Supplier");

        product = new Product();
        product.setId(productId);

        price = new Price();
        price.setProduct(product);
        price.setPricePerKg(new BigDecimal("50.00"));

        shipment = new Shipment();
        shipment.setId(UUID.randomUUID());
        shipment.setSupplier(supplier);
        shipment.setShipmentDate(LocalDate.now());

        shipmentItemRequests = List.of(new ShipmentItemRequest(productId, new BigDecimal("10")));
    }

    @Test
    void createShipment_Success() {
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(productRepository.findAllById(any())).thenReturn(List.of(product));
        when(priceRepository.findPriceForSupplierProducts(eq(supplierId), any(), any()))
                .thenReturn(List.of(price));
        when(shipmentRepository.save(any())).thenReturn(shipment);

        UUID result = shipmentService.createShipment(supplierId, LocalDate.now(), shipmentItemRequests);

        assertNotNull(result);
        assertEquals(shipment.getId(), result);
        verify(shipmentRepository, times(1)).save(any());
    }

    @Test
    void createShipment_ThrowsEntityNotFoundException_WhenSupplierNotFound() {
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        Executable executable = () -> shipmentService.createShipment(supplierId, LocalDate.now(), shipmentItemRequests);

        assertThrows(EntityNotFoundException.class, executable);
    }


    @Test
    void updateShipment_Success() {
        when(shipmentRepository.findFetchById(any())).thenReturn(Optional.of(shipment));
        when(productRepository.findAllById(any())).thenReturn(List.of(product));
        when(priceRepository.findPriceForSupplierProducts(eq(supplierId), any(), any()))
                .thenReturn(List.of(price));
        when(shipmentRepository.save(any())).thenReturn(shipment);

        UUID result = shipmentService.updateShipment(shipment.getId(), supplierId,
                LocalDate.now(), shipmentItemRequests);

        assertNotNull(result);
        assertEquals(shipment.getId(), result);
        verify(shipmentRepository, times(1)).save(any());
    }

    @Test
    void updateShipment_ThrowsAccessDeniedException_WhenSupplierMismatch() {
        UUID anotherSupplierId = UUID.randomUUID();
        when(shipmentRepository.findFetchById(any())).thenReturn(Optional.of(shipment));

        Executable executable = () -> shipmentService.updateShipment(shipment.getId(), anotherSupplierId,
                LocalDate.now(), shipmentItemRequests);

        assertThrows(AccessDeniedException.class, executable);
    }

    @Test
    void getShipmentsReport_Success() {
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        ShipmentItemDto shipmentItemDto = new ShipmentItemDto(supplierId,
                "Test Supplier", product.getId(), "Product Name", new BigDecimal("10"),
                new BigDecimal("234"));
        List<ShipmentItemDto> listShipmentItemDto = List.of(shipmentItemDto);

        when(shipmentRepository.getProductShipmentSummary(startDate, endDate)).thenReturn(listShipmentItemDto);
        when(supplierRepository.findAllById(any())).thenReturn(List.of(supplier));

        List<ShipmentReportDto> result = shipmentService.getShipmentsReport(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(supplierId, result.getFirst().getSupplierId());
        assertEquals("Test Supplier", result.getFirst().getSupplierName());
        assertFalse(result.getFirst().getShipments().isEmpty());
    }

    @Test
    void getShipmentsReport_ReturnsEmptyList_WhenNoShipments() {
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        when(shipmentRepository.getProductShipmentSummary(startDate, endDate)).thenReturn(Collections.emptyList());

        List<ShipmentReportDto> result = shipmentService.getShipmentsReport(startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getShipmentItemsReports_Success() {
        UUID shipmentId = shipment.getId();

        ShipmentItemReportGraphDto graphDto = new ShipmentItemReportGraphDto(product.getId(),
                12.2f, 1f, 12.2f);
        ShipmentItem shipmentItem = new ShipmentItem();
        shipmentItem.setProduct(product);
        shipmentItem.setWeightKg(new BigDecimal("10"));

        when(shipmentItemRepository.findAllByShipmentId(shipmentId)).thenReturn(List.of(shipmentItem));
        when(shipmentMapper.toGraphDto(shipmentItem)).thenReturn(graphDto);

        List<ShipmentItemReportGraphDto> result = shipmentService.getShipmentItemsReports(shipmentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product.getId(), result.getFirst().getProductId());
    }

    @Test
    void getShipmentItemsReports_ReturnsEmptyList_WhenNoItems() {
        UUID shipmentId = shipment.getId();

        when(shipmentItemRepository.findAllByShipmentId(shipmentId)).thenReturn(Collections.emptyList());

        List<ShipmentItemReportGraphDto> result = shipmentService.getShipmentItemsReports(shipmentId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}