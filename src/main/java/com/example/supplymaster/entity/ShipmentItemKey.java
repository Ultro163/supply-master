package com.example.supplymaster.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Ключ составного первичного ключа для сущности ShipmentItem.
 */
@Getter
@Setter
@Embeddable
public class ShipmentItemKey implements Serializable {
    @Serial
    private static final long serialVersionUID = 6366034607451709323L;
    @NotNull
    @Column(name = "shipment_id", nullable = false)
    private UUID shipmentId;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ShipmentItemKey entity = (ShipmentItemKey) o;
        return Objects.equals(this.productId, entity.productId) &&
                Objects.equals(this.shipmentId, entity.shipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, shipmentId);
    }
}