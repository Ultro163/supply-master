package com.example.supplymaster.service.price;

import com.example.supplymaster.dto.mappers.PriceMapper;
import com.example.supplymaster.dto.price.PriceDto;
import com.example.supplymaster.dto.product.ProductDto;
import com.example.supplymaster.dto.supplier.SupplierDto;
import com.example.supplymaster.entity.Price;
import com.example.supplymaster.error.exception.EntityNotFoundException;
import com.example.supplymaster.error.exception.PriceConflictException;
import com.example.supplymaster.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceImplTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private PriceMapper priceMapper;

    @InjectMocks
    private PriceServiceImpl priceService;

    private PriceDto priceDto;
    private Price price;

    @BeforeEach
    void setUp() {
        priceDto = new PriceDto();
        priceDto.setId(UUID.randomUUID());
        priceDto.setPricePerKg(new BigDecimal("100.00"));

        SupplierDto supplierDto = new SupplierDto();
        supplierDto.setId(UUID.randomUUID());
        priceDto.setSupplier(supplierDto);

        ProductDto productDto = new ProductDto();
        productDto.setId(UUID.randomUUID());
        priceDto.setProduct(productDto);

        price = new Price();
        price.setId(priceDto.getId());
        price.setPricePerKg(priceDto.getPricePerKg());
    }

    @Test
    void createPriceForProduct_Success() {
        when(priceRepository.checkIsNoIntersectionTime(any(), any(), any(), any())).thenReturn(true);
        when(priceMapper.toEntity(any())).thenReturn(price);
        when(priceRepository.save(any())).thenReturn(price);

        UUID result = priceService.createPriceForProduct(priceDto);

        assertNotNull(result);
        assertEquals(price.getId(), result);
        verify(priceRepository, times(1)).save(any());
    }

    @Test
    void createPriceForProduct_ThrowsPriceConflictException() {
        when(priceRepository.checkIsNoIntersectionTime(any(), any(), any(), any())).thenReturn(false);

        assertThrows(PriceConflictException.class, () -> priceService.createPriceForProduct(priceDto));
        verify(priceRepository, never()).save(any());
    }

    @Test
    void updatePriceForProduct_Success() {
        when(priceRepository.findById(any())).thenReturn(Optional.of(price));

        UUID result = priceService.updatePriceForProduct(priceDto);

        assertNotNull(result);
        assertEquals(price.getId(), result);
        assertEquals(priceDto.getPricePerKg(), price.getPricePerKg());
    }

    @Test
    void updatePriceForProduct_ThrowsEntityNotFoundException() {
        when(priceRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> priceService.updatePriceForProduct(priceDto));
    }
}