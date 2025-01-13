package org.pizzeria.fabulosa.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pizzeria.fabulosa.services.order.OrderService;
import org.pizzeria.fabulosa.web.constants.ValidationResponses;
import org.pizzeria.fabulosa.web.order.validation.OrderValidationResult;
import org.pizzeria.fabulosa.web.order.validation.OrderValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDeleteValidationTest {

	@Mock
	private OrderService orderService;

	@InjectMocks
	private OrderValidator orderValidator;

	@Test
	void givenOrderDeleteRequest_whenDeleteWindowPassed_thenReturnInvalidResult() {
		when(orderService.findCreatedOnById(1L)).thenReturn(LocalDateTime.now().minusMinutes(20));

		OrderValidationResult isDeleteRequestValid = orderValidator.validateDelete(1L);

		assertAll(() -> {
			assertFalse(isDeleteRequestValid.isValid());
			assertEquals(ValidationResponses.ORDER_DELETE_TIME_ERROR, isDeleteRequestValid.getMessage());
		});

		verify(orderService, times(1)).findCreatedOnById(1L);
	}

	@Test
	void givenOrderDeleteRequest_whenDeleteWindowDidNotPass_thenReturnValidResult() {
		when(orderService.findCreatedOnById(1L)).thenReturn(LocalDateTime.now().minusMinutes(9));

		OrderValidationResult isDeleteRequestValid = orderValidator.validateDelete(1L);

		assertAll(() -> {
			assertTrue(isDeleteRequestValid.isValid());
			assertNull(isDeleteRequestValid.getMessage());
		});

		verify(orderService, times(1)).findCreatedOnById(1L);
	}
}