package org.pizzeria.fabulosa.services.order;

import org.pizzeria.fabulosa.entity.order.Order;
import org.pizzeria.fabulosa.web.dto.order.dto.CreatedOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.NewAnonOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.NewUserOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.OrderDTO;
import org.pizzeria.fabulosa.web.dto.order.projection.OrderSummaryProjection;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderService {

	Optional<OrderDTO> findProjectionById(Long orderId);

	CreatedOrderDTO createAnonOrder(NewAnonOrderDTO newAnonOrder);

	CreatedOrderDTO createUserOrder(Long userId, NewUserOrderDTO newUserOrder);

	void deleteUserOrderById(Long orderId);

	Page<OrderSummaryProjection> findUserOrderSummary(Long userId, int size, int page);

	// info - for internal use only

	Optional<Order> findUserOrderById(Long orderId);

	LocalDateTime findCreatedOnById(Long orderId);
}