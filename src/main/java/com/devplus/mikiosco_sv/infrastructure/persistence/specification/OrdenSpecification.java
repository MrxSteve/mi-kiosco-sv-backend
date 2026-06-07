package com.devplus.mikiosco_sv.infrastructure.persistence.specification;

import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.OrdenEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class OrdenSpecification {

    private OrdenSpecification() {}

    public static Specification<OrdenEntity> hasComedor(UUID comedorId) {
        return (root, q, cb) -> cb.equal(root.get("comedorId"), comedorId);
    }

    public static Specification<OrdenEntity> hasOrderNumber(Long orderNumber) {
        return (root, q, cb) -> cb.equal(root.get("orderNumber"), orderNumber);
    }

    public static Specification<OrdenEntity> hasStatus(OrderStatus status) {
        return (root, q, cb) -> cb.equal(root.get("status"), status);
    }

    /** Órdenes con clienteId IS NULL y customerName IS NULL (completamente anónimas). */
    public static Specification<OrdenEntity> isAnonymous() {
        return (root, q, cb) -> cb.and(
                cb.isNull(root.get("clienteId")),
                cb.isNull(root.get("customerName"))
        );
    }

    /** Órdenes con promoción aplicada. */
    public static Specification<OrdenEntity> hasPromotion() {
        return (root, q, cb) -> cb.isNotNull(root.get("promocionId"));
    }

    /** Órdenes con descuento real > 0 (puede haber promo aplicada con descuento 0 en edge cases). */
    public static Specification<OrdenEntity> hasDiscount() {
        return (root, q, cb) -> cb.greaterThan(root.get("discountAmount"),
                new java.math.BigDecimal("0.00"));
    }

    /** Búsqueda en customerName libre (órdenes con nombre, no registradas). */
    public static Specification<OrdenEntity> customerNameLike(String q) {
        return (root, query, cb) -> {
            var namePath = root.<String>get("customerName");
            return cb.like(cb.lower(namePath), "%" + q.toLowerCase() + "%");
        };
    }

    /** Órdenes vinculadas a alguno de los clientes dados. */
    public static Specification<OrdenEntity> clienteIdIn(List<UUID> clienteIds) {
        return (root, q, cb) -> root.get("clienteId").in(clienteIds);
    }

    public static Specification<OrdenEntity> createdAfter(OffsetDateTime from) {
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<OrdenEntity> createdBefore(OffsetDateTime to) {
        return (root, q, cb) -> cb.lessThan(root.get("createdAt"), to);
    }
}
