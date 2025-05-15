package org.pizzeria.fabulosa.common.dao.order;

import org.pizzeria.fabulosa.common.entity.order.Order;
import org.pizzeria.fabulosa.web.dto.order.dto.CreatedOnDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.OrderDTO;
import org.pizzeria.fabulosa.web.dto.order.projection.OrderSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<OrderDTO> findOrderDTOById(Long orderId);

	Page<OrderSummaryProjection> findAllByUser_Id(Long userId, Pageable pageable);

	Optional<CreatedOnDTO> findCreatedOnById(Long orderId);
}