package com.example.supplymaster.dto.supplier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO для представления информации о поставщике.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDto {
    private UUID id;
    private String name;
}