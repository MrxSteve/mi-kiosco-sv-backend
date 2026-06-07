package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.*;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.*;
import com.devplus.mikiosco_sv.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Ensambla ProductResponse desde múltiples repositorios en un número fijo de queries,
 * independiente de cuántos productos se listen.
 */
@Component
@RequiredArgsConstructor
public class ProductAssembler {

    private final CategoriaRepository categoriaRepository;
    private final ProductoComboRepository comboRepository;
    private final ProductoExtraRepository productoExtraRepository;
    private final ExtraRepository extraRepository;
    private final GrupoExtraRepository grupoExtraRepository;

    /** Ensambla una lista de productos de forma eficiente (O(queries fijos), no N+1). */
    public List<ProductResponse> assembleList(List<ProductoEntity> productos, UUID comedorId) {
        if (productos.isEmpty()) return List.of();

        List<UUID> productIds = productos.stream().map(ProductoEntity::getId).toList();

        // Categorías del comedor (para nombre)
        Map<UUID, String> categoryNames = categoriaRepository
                .findByComedorIdOrderByDisplayOrderAscNameAsc(comedorId)
                .stream()
                .collect(Collectors.toMap(CategoriaEntity::getId, CategoriaEntity::getName));

        // Combos agrupados por productoId
        Map<UUID, List<ProductoComboEntity>> combosByProduct = comboRepository
                .findByProductoIdInAndComedorId(productIds, comedorId)
                .stream()
                .collect(Collectors.groupingBy(ProductoComboEntity::getProductoId));

        // Relaciones producto-extra
        Map<UUID, List<UUID>> extraIdsByProduct = productoExtraRepository
                .findByIdProductoIdIn(productIds)
                .stream()
                .collect(Collectors.groupingBy(
                        pe -> pe.getId().getProductoId(),
                        Collectors.mapping(pe -> pe.getId().getExtraId(), Collectors.toList())
                ));

        // Todos los extras referenciados
        List<UUID> allExtraIds = extraIdsByProduct.values().stream()
                .flatMap(Collection::stream)
                .distinct()
                .toList();
        Map<UUID, ExtraEntity> extrasById = allExtraIds.isEmpty()
                ? Map.of()
                : extraRepository.findByIdInAndComedorId(allExtraIds, comedorId)
                        .stream().collect(Collectors.toMap(ExtraEntity::getId, e -> e));

        // Grupos de extras
        Set<UUID> grupoIds = extrasById.values().stream()
                .map(ExtraEntity::getGrupoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, String> grupoNames = grupoIds.isEmpty()
                ? Map.of()
                : grupoExtraRepository.findAllById(grupoIds)
                        .stream().collect(Collectors.toMap(GrupoExtraEntity::getId, GrupoExtraEntity::getName));

        return productos.stream()
                .map(p -> buildResponse(p,
                        categoryNames.getOrDefault(p.getCategoriaId(), "Sin categoría"),
                        combosByProduct.getOrDefault(p.getId(), List.of()),
                        extraIdsByProduct.getOrDefault(p.getId(), List.of()),
                        extrasById,
                        grupoNames))
                .toList();
    }

    /** Ensambla un único producto (para GET /products/{id}). */
    public ProductResponse assembleSingle(ProductoEntity producto, UUID comedorId) {
        return assembleList(List.of(producto), comedorId).get(0);
    }

    private ProductResponse buildResponse(
            ProductoEntity p,
            String categoryName,
            List<ProductoComboEntity> combos,
            List<UUID> extraIds,
            Map<UUID, ExtraEntity> extrasById,
            Map<UUID, String> grupoNames) {

        List<ComboResponse> comboResponses = combos.stream()
                .map(c -> ComboResponse.builder()
                        .id(c.getId())
                        .label(c.getLabel())
                        .comboQty(c.getComboQty())
                        .comboPrice(c.getComboPrice())
                        .status(c.getStatus())
                        .build())
                .toList();

        // Agrupar extras asignados a este producto por grupo
        Map<UUID, List<ExtraEntity>> byGroup = new LinkedHashMap<>(); // null key = sin grupo
        for (UUID extraId : extraIds) {
            ExtraEntity extra = extrasById.get(extraId);
            if (extra == null || extra.getStatus() != GenericStatus.ACTIVE) continue;
            byGroup.computeIfAbsent(extra.getGrupoId(), k -> new ArrayList<>()).add(extra);
        }

        List<ProductExtraGroupResponse> extraGroups = byGroup.entrySet().stream()
                .map(entry -> ProductExtraGroupResponse.builder()
                        .groupId(entry.getKey())
                        .groupName(entry.getKey() == null ? "General"
                                : grupoNames.getOrDefault(entry.getKey(), "General"))
                        .extras(entry.getValue().stream()
                                .map(e -> ProductExtraGroupResponse.Item.builder()
                                        .id(e.getId())
                                        .name(e.getName())
                                        .price(e.getPrice())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .imageUrl(p.getImageUrl())
                .categoryId(p.getCategoriaId())
                .categoryName(categoryName)
                .hasExtras(p.isHasExtras())
                .hasCombos(p.isHasCombos())
                .status(p.getStatus())
                .combos(comboResponses)
                .extraGroups(extraGroups)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
