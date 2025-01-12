package org.pizzeria.fabulosa.repos.order;

import org.pizzeria.fabulosa.entity.order.Order;
import org.pizzeria.fabulosa.web.dto.order.dto.CreatedOnDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.OrderDTO;
import org.pizzeria.fabulosa.web.dto.order.projection.OrderSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<OrderDTO> findOrderById(Long orderId);

	Page<OrderSummaryProjection> findAllByUser_Id(Long userId, Pageable pageable);

	// internal use only

	@Query("from Order order " +
			"join fetch order.orderDetails " +
			"join fetch order.address " +
			"join fetch order.cart as cart " +
			"join fetch cart.cartItems " +
			"left join fetch order.user " +
			"where order.id = :orderId")
	Optional<Order> findUserOrderById(Long orderId);

	Optional<CreatedOnDTO> findCreatedOnById(Long orderId);
}