package org.pizzeria.fabulosa.web.aop.aspects.validation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.pizzeria.fabulosa.entity.error.Error;
import org.pizzeria.fabulosa.repos.order.OrderRepository;
import org.pizzeria.fabulosa.web.constants.ApiResponses;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.api.Status;
import org.pizzeria.fabulosa.web.dto.order.dto.CreatedOnDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.NewAnonOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.NewUserOrderDTO;
import org.pizzeria.fabulosa.web.order.validation.OrderValidationResult;
import org.pizzeria.fabulosa.web.order.validation.OrderValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Aspect
public class ValidateOrderOperation {

	private final OrderRepository orderRepository;

	@Around(value = "execution(* org.pizzeria.fabulosa.web.controllers.open.AnonController.createAnonOrder(..)) && args(newAnonOrder, request)", argNames = "pjp,newAnonOrder,request")
	public Object validateNewAnonOrder(ProceedingJoinPoint pjp, NewAnonOrderDTO newAnonOrder, HttpServletRequest request) throws Throwable {
		OrderValidationResult result = OrderValidator.validate(newAnonOrder.cart(), newAnonOrder.orderDetails());

		if (result.isValid()) {
			return pjp.proceed();
		}

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.BAD_REQUEST.name())
						.code(HttpStatus.BAD_REQUEST.value())
						.isError(true)
						.build())
				.error(Error.builder()
						.id(UUID.randomUUID().getMostSignificantBits())
						.cause(result.getMessage())
						.origin(ValidateOrderOperation.class.getSimpleName() + ".validateNewAnonOrder")
						.path(request.getPathInfo())
						.logged(false)
						.fatal(false)
						.build())
				.build();

		return ResponseEntity.status(HttpStatus.OK).body(response); // return OK to get the ResponseDTO in onSuccess callback
	}

	@Around(value = "execution(* org.pizzeria.fabulosa.web.controllers.user.UserOrdersController.createUserOrder(..)) && args" +
			"(order, userId, request)", argNames = "pjp,order,userId,request")
	public Object validateNewUserOrder(ProceedingJoinPoint pjp, NewUserOrderDTO order, Long userId, HttpServletRequest request) throws Throwable {
		OrderValidationResult result = OrderValidator.validate(order.cart(), order.orderDetails());

		if (result.isValid()) {
			return pjp.proceed();
		}

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.BAD_REQUEST.name())
						.code(HttpStatus.BAD_REQUEST.value())
						.isError(true)
						.build())
				.error(Error.builder()
						.id(UUID.randomUUID().getMostSignificantBits())
						.cause(result.getMessage())
						.origin(ValidateOrderOperation.class.getSimpleName() + ".validateNewUserOrder")
						.path(request.getPathInfo())
						.logged(false)
						.fatal(false)
						.build())
				.build();

		return ResponseEntity.status(HttpStatus.OK).body(response); // return OK to get the ResponseDTO in onSuccess callback
	}

	@Around(value = "execution(* org.pizzeria.fabulosa.web.controllers.user.UserOrdersController.deleteUserOrderById(..)) && args" +
			"(orderId,userId,request)", argNames = "pjp,orderId,userId,request")
	public Object validateUserOrderDelete(ProceedingJoinPoint pjp, Long orderId, Long userId, HttpServletRequest request) throws Throwable {

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.BAD_REQUEST.name())
						.code(HttpStatus.BAD_REQUEST.value())
						.isError(true)
						.build())
				.error(Error.builder()
						.id(UUID.randomUUID().getMostSignificantBits())
						.origin(ValidateOrderOperation.class.getSimpleName() + ".validateUserOrderDelete")
						.path(request.getPathInfo())
						.logged(false)
						.fatal(false)
						.build())
				.build();

		Optional<CreatedOnDTO> createdOnById = orderRepository.findCreatedOnById(orderId);

		// return OK to get the ResponseDTO in onSuccess callback
		if (createdOnById.isPresent()) {
			OrderValidationResult result = OrderValidator.validateDelete(createdOnById.get().createdOn());
			if (result.isValid()) {
				return pjp.proceed();
			}
			response.getError().setCause(result.getMessage());
		} else {
			response.getError().setCause(ApiResponses.ORDER_NOT_FOUND);
		}

		return ResponseEntity.status(HttpStatus.OK).body(response); // return OK to get the ResponseDTO in onSuccess callback
	}
}