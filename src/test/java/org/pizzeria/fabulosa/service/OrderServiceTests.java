package org.pizzeria.fabulosa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.services.address.AddressService;
import org.pizzeria.fabulosa.services.order.OrderService;
import org.pizzeria.fabulosa.services.user.UserService;
import org.pizzeria.fabulosa.utils.TestUtils;
import org.pizzeria.fabulosa.utils.enums.OrderState;
import org.pizzeria.fabulosa.web.dto.order.dto.CreatedOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "file:src/test/resources/db/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(transactionMode = ISOLATED))
@Sql(scripts = "file:src/test/resources/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = ISOLATED))
@DirtiesContext
public class OrderServiceTests {

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private AddressService addressService;

	@Test
	void givenUserOrder_thenCancelOrder() {

		// Arrange

		User user = TestUtils.createUser(userService);
		Long addressId = TestUtils.createAddress("Street", 87, addressService);
		CreatedOrderDTO order = TestUtils.createUserOrder(orderService, user, addressId);
		assertThat(order.state()).isNotNull();
		assertThat(order.state()).isEqualTo(OrderState.PREPARING);
		Long id = order.id();

		// Act

		orderService.setState(id, OrderState.CANCELLED);

		// Assert

		Optional<OrderDTO> updatedOrder = orderService.findOrderDTOById(id);
		assertThat(updatedOrder).isPresent();
		assertThat(updatedOrder.get().state()).isEqualTo(OrderState.CANCELLED);
	}
}
