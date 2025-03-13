package com.example.supplymaster.service.price;

import com.example.supplymaster.dto.price.PriceDto;
import com.example.supplymaster.dto.product.ProductDto;
import com.example.supplymaster.dto.supplier.SupplierDto;
import com.example.supplymaster.entity.Price;
import com.example.supplymaster.entity.Product;
import com.example.supplymaster.entity.Supplier;
import com.example.supplymaster.error.exception.EntityNotFoundException;
import com.example.supplymaster.error.exception.PriceConflictException;
import com.example.supplymaster.repository.PriceRepository;
import com.example.supplymaster.repository.ProductRepository;
import com.example.supplymaster.repository.SupplierRepository;
import com.example.supplymaster.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class PriceServiceImplIT {

    @Autowired
    private PriceService priceService;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void cleanDb() {
        priceRepository.deleteAll();
        supplierRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void createPriceForProduct_shouldSavePrice() {
        Supplier supplier = TestUtil.createTestSupplier();
        Product product = TestUtil.createTestProduct();

        SupplierDto supplierDto = new SupplierDto();
        supplierDto.setId(supplier.getId());

        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());

        PriceDto priceDto = new PriceDto();
        priceDto.setSupplier(supplierDto);
        priceDto.setProduct(productDto);
        priceDto.setPricePerKg(new BigDecimal("100.50"));
        priceDto.setStartDate(LocalDate.of(2024, 3, 1));
        priceDto.setEndDate(LocalDate.of(2024, 4, 1));

        UUID priceId = priceService.createPriceForProduct(priceDto);
        assertNotNull(priceId);

        Price savedPrice = priceRepository.findById(priceId).orElse(null);
        assertNotNull(savedPrice);
        assertEquals(new BigDecimal("100.50"), savedPrice.getPricePerKg());
    }

    @Test
    void createPriceForProduct_shouldThrowConflictException() {
        Supplier supplier = TestUtil.createTestSupplier();
        Product product = TestUtil.createTestProduct();

        SupplierDto supplierDto = new SupplierDto();
        supplierDto.setId(supplier.getId());

        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());

        PriceDto firstPrice = new PriceDto();
        firstPrice.setSupplier(supplierDto);
        firstPrice.setProduct(productDto);
        firstPrice.setPricePerKg(new BigDecimal("100.50"));
        firstPrice.setStartDate(LocalDate.of(2024, 3, 1));
        firstPrice.setEndDate(LocalDate.of(2024, 4, 1));

        priceService.createPriceForProduct(firstPrice);

        PriceDto conflictingPrice = new PriceDto();
        conflictingPrice.setSupplier(supplierDto);
        conflictingPrice.setProduct(productDto);
        conflictingPrice.setPricePerKg(new BigDecimal("105.00"));
        conflictingPrice.setStartDate(LocalDate.of(2024, 3, 15));
        conflictingPrice.setEndDate(LocalDate.of(2024, 4, 15));

        assertThrows(PriceConflictException.class, () -> priceService.createPriceForProduct(conflictingPrice));
    }

    @Test
    void updatePriceForProduct_shouldUpdatePrice() {
        Supplier supplier = TestUtil.createTestSupplier();
        Product product = TestUtil.createTestProduct();

        Price price = new Price();
        price.setSupplier(supplier);
        price.setProduct(product);
        price.setPricePerKg(new BigDecimal("100.50"));
        price.setStartDate(LocalDate.of(2024, 3, 1));
        price.setEndDate(LocalDate.of(2024, 4, 1));
        price = priceRepository.save(price);

        PriceDto priceDto = new PriceDto();
        priceDto.setId(price.getId());
        priceDto.setPricePerKg(new BigDecimal("120.75"));

        UUID updatedPriceId = priceService.updatePriceForProduct(priceDto);
        assertNotNull(updatedPriceId);

        Price updatedPrice = priceRepository.findById(updatedPriceId).orElse(null);
        assertNotNull(updatedPrice);
        assertEquals(new BigDecimal("120.75"), updatedPrice.getPricePerKg());
    }

    @Test
    void updatePriceForProduct_shouldThrowEntityNotFoundException() {
        PriceDto priceDto = new PriceDto();
        priceDto.setId(UUID.randomUUID());
        priceDto.setPricePerKg(new BigDecimal("150.00"));

        assertThrows(EntityNotFoundException.class, () -> priceService.updatePriceForProduct(priceDto));
    }
}