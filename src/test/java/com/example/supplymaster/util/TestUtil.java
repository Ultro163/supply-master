package com.example.supplymaster.util;

import com.example.supplymaster.entity.Product;
import com.example.supplymaster.entity.Supplier;
import com.example.supplymaster.repository.ProductRepository;
import com.example.supplymaster.repository.SupplierRepository;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {

    private static SupplierRepository supplierRepository;
    private static ProductRepository productRepository;

    public TestUtil(SupplierRepository supplierRepository, ProductRepository productRepository) {
        TestUtil.supplierRepository = supplierRepository;
        TestUtil.productRepository = productRepository;
    }

    public static Supplier createTestSupplier() {
        Supplier supplier = new Supplier();
        supplier.setName("Test Supplier");
        return supplierRepository.save(supplier);
    }


    public static Product createTestProduct() {
        Product product = new Product();
        product.setName("Test Product");
        return productRepository.save(product);
    }
}