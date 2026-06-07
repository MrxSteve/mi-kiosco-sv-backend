package com.devplus.mikiosco_sv.application.usecase.cocina;

import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.OrdenRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.KitchenOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListKitchenOrdersUseCase {

    private final OrdenRepository ordenRepository;
    private final KitchenOrderAssembler assembler;

    private static final List<OrderStatus> KITCHEN_STATUSES =
            List.of(OrderStatus.PENDING, OrderStatus.IN_PREPARATION);

    @Transactional(readOnly = true)
    public List<KitchenOrderResponse> execute(AuthenticatedUser caller) {
        var ordenes = ordenRepository
                .findByComedorIdAndSentToKitchenAtIsNotNullAndStatusInOrderBySentToKitchenAtAsc(
                        caller.comedorId(), KITCHEN_STATUSES);
        return assembler.assembleList(ordenes);
    }
}
