package com.devplus.mikiosco_sv.application.usecase.orden;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.*;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.*;
import com.devplus.mikiosco_sv.presentation.dto.response.OrderItemResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Ensambla OrderResponse desde múltiples repositorios en un número fijo de queries.
 */
@Component
@RequiredArgsConstructor
public class OrderAssembler {

    private final OrdenDetalleRepository detalleRepository;
    private final OrdenDetalleExtraRepository detalleExtraRepository;
    private final PagoRepository pagoRepository;
    private final ClienteRepository clienteRepository;
    private final PromocionRepository promocionRepository;

    public OrderResponse assembleSingle(OrdenEntity orden) {
        return assembleList(List.of(orden)).get(0);
    }

    public List<OrderResponse> assembleList(List<OrdenEntity> ordenes) {
        if (ordenes.isEmpty()) return List.of();

        List<UUID> ordenIds = ordenes.stream().map(OrdenEntity::getId).toList();

        // Detalles agrupados por ordenId
        Map<UUID, List<OrdenDetalleEntity>> detallesByOrden = detalleRepository
                .findByOrdenIdInOrderByCreatedAtAsc(ordenIds)
                .stream()
                .collect(Collectors.groupingBy(OrdenDetalleEntity::getOrdenId));

        // Extras agrupados por detalleId
        List<UUID> detalleIds = detallesByOrden.values().stream()
                .flatMap(Collection::stream)
                .map(OrdenDetalleEntity::getId)
                .toList();
        Map<UUID, List<OrdenDetalleExtraEntity>> extrasByDetalle = detalleIds.isEmpty()
                ? Map.of()
                : detalleExtraRepository.findByOrdenDetalleIdIn(detalleIds)
                        .stream()
                        .collect(Collectors.groupingBy(OrdenDetalleExtraEntity::getOrdenDetalleId));

        // Pagos por ordenId
        Map<UUID, PagoEntity> pagoByOrden = pagoRepository.findByOrdenIdIn(ordenIds)
                .stream()
                .collect(Collectors.toMap(PagoEntity::getOrdenId, p -> p));

        // Clientes (para nombre y código)
        Set<UUID> clienteIds = ordenes.stream()
                .map(OrdenEntity::getClienteId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, ClienteEntity> clientesById = clienteIds.isEmpty()
                ? Map.of()
                : clienteRepository.findAllById(clienteIds).stream()
                        .collect(Collectors.toMap(ClienteEntity::getId, c -> c));

        // Promociones (para el código)
        Set<UUID> promoIds = ordenes.stream()
                .map(OrdenEntity::getPromocionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, String> promoCodeById = promoIds.isEmpty()
                ? Map.of()
                : promocionRepository.findAllById(promoIds).stream()
                        .collect(Collectors.toMap(PromocionEntity::getId, PromocionEntity::getCode));

        return ordenes.stream()
                .map(o -> build(o,
                        detallesByOrden.getOrDefault(o.getId(), List.of()),
                        extrasByDetalle,
                        pagoByOrden.get(o.getId()),
                        clientesById.get(o.getClienteId()),
                        o.getPromocionId() == null ? null : promoCodeById.get(o.getPromocionId())))
                .toList();
    }

    private OrderResponse build(
            OrdenEntity orden,
            List<OrdenDetalleEntity> detalles,
            Map<UUID, List<OrdenDetalleExtraEntity>> extrasByDetalle,
            PagoEntity pago,
            ClienteEntity cliente,
            String promoCode) {

        List<OrderItemResponse> items = detalles.stream()
                .map(d -> buildItem(d, extrasByDetalle.getOrDefault(d.getId(), List.of())))
                .toList();

        String displayName = cliente != null
                ? cliente.getFullName()
                : (orden.getCustomerName() != null ? orden.getCustomerName() : "Anónimo");

        OrderResponse.PaymentInfo paymentInfo = pago == null ? null
                : OrderResponse.PaymentInfo.builder()
                        .amountReceived(pago.getAmountReceived())
                        .changeAmount(pago.getChangeAmount())
                        .method(pago.getMethod())
                        .paidAt(pago.getPaidAt())
                        .build();

        return OrderResponse.builder()
                .id(orden.getId())
                .orderNumber(orden.getOrderNumber())
                .status(orden.getStatus())
                .sentToKitchenAt(orden.getSentToKitchenAt())
                .clienteId(orden.getClienteId())
                .customerDisplayName(displayName)
                .customerCode(cliente != null ? cliente.getCustomerCode() : null)
                .notes(orden.getNotes())
                .items(items)
                .subtotal(orden.getSubtotal())
                .discountAmount(orden.getDiscountAmount())
                .promotionCode(promoCode)
                .total(orden.getTotalAmount())
                .payment(paymentInfo)
                .createdAt(orden.getCreatedAt())
                .updatedAt(orden.getUpdatedAt())
                .build();
    }

    private OrderItemResponse buildItem(OrdenDetalleEntity d, List<OrdenDetalleExtraEntity> extras) {
        OrderItemResponse.ComboInfo comboInfo = d.getComboId() == null ? null
                : OrderItemResponse.ComboInfo.builder()
                        .comboId(d.getComboId())
                        .label(null) // snapshot del label no está almacenado; se puede agregar si se necesita
                        .comboPrice(d.getComboPriceSnapshot())
                        .build();

        List<OrderItemResponse.ExtraInfo> extraInfos = extras.stream()
                .map(e -> OrderItemResponse.ExtraInfo.builder()
                        .extraId(e.getExtraId())
                        .name(e.getExtraNameSnapshot())
                        .price(e.getExtraPriceSnapshot())
                        .build())
                .toList();

        return OrderItemResponse.builder()
                .id(d.getId())
                .productId(d.getProductoId())
                .productName(d.getProductNameSnapshot())
                .quantity(d.getQuantity())
                .unitPrice(d.getUnitPriceSnapshot())
                .combo(comboInfo)
                .extras(extraInfos)
                .lineSubtotal(d.getLineSubtotal())
                .notes(d.getNotes())
                .build();
    }
}
