package org.pizzeria.fabulosa.web.service.order;

import org.pizzeria.fabulosa.web.dto.order.dto.*;
import org.pizzeria.fabulosa.web.dto.order.projection.OrderSummaryProjection;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface OrderService {

	Optional<OrderDTO> findOrderDTOById(Long orderId);

	CreatedOrderDTO createAnonOrder(NewAnonOrderDTO newAnonOrder);

	CreatedOrderDTO createUserOrder(Long userId, NewUserOrderDTO newUserOrder);

	void deleteUserOrderById(Long orderId);

	Page<OrderSummaryProjection> findUserOrderSummary(Long userId, int size, int page);

	// for internal use only

	Optional<CreatedOnDTO> findCreatedOnDTOById(Long orderId);
}