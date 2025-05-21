package org.pizzeria.fabulosa.web.service.order;

import org.pizzeria.fabulosa.common.entity.projection.CreatedOnProjection;
import org.pizzeria.fabulosa.common.entity.projection.OrderSummaryProjection;
import org.pizzeria.fabulosa.web.dto.order.CreatedOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.NewAnonOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.NewUserOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.OrderDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface OrderService {

	Optional<OrderDTO> findOrderDTOById(Long orderId);

	CreatedOrderDTO createAnonOrder(NewAnonOrderDTO newAnonOrder);

	CreatedOrderDTO createUserOrder(Long userId, NewUserOrderDTO newUserOrder);

	void deleteUserOrderById(Long orderId);

	Page<OrderSummaryProjection> findUserOrderSummary(Long userId, int size, int page);

	// for internal use only

	Optional<CreatedOnProjection> findCreatedOnDTOById(Long orderId);
}