package PizzaApp.api.validation.order;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import PizzaApp.api.exceptions.exceptions.order.*;
import org.springframework.stereotype.Component;
import PizzaApp.api.entity.order.Order;

// Could turn this into an Util class

@Component
public class OrderValidatorImpl implements OrderValidator {

	private final Logger logger = Logger.getLogger(getClass().getName());
	private LocalDateTime now;
	private LocalDateTime createdOn;

	public OrderValidatorImpl setCurrentTime() {
		setNow(LocalDateTime.now());
		return this;
	}

	@Override
	public void validateUpdate(Order order) {
		validate(order);
		setCreatedOn(order.getCreatedOn());
		isCartUpdateTimeLimitValid(order); // FIXME -  off for dev (unless testing)
		isOrderDataUpdateTimeLimitValid(); // FIXME - off for dev (unless testing)
	}

	@Override
	public void validate(Order order) {
		//isRequestWithinWorkingHours(); // FIXME - on for prod
		isCartValid(order);
		isChangeRequestedValid(order);
		calculatePaymentChange(order);
	}


	@Override
	public void isCartUpdateTimeLimitValid(Order order) {
		LocalDateTime cartUpdateTimeLimit = createdOn.plusMinutes(10);
		if (now.isAfter(cartUpdateTimeLimit)) {
			order.setCart(null);
		}
	}

	@Override
	public void isOrderDataUpdateTimeLimitValid() {
		LocalDateTime orderDataUpdateTimeLimit = createdOn.plusMinutes(15);
		if (now.isAfter(orderDataUpdateTimeLimit)) {
			logger.info(String.format(
					"Order data update is not allowed (createdOn: %s | now: %s) ", createdOn, now));
			throw new OrderDataUpdateTimeLimitException(
					"El tiempo límite para actualizar el pedido (15 minutos) ha finalizado");
		}
	}

	@Override
	public void isOrderDeleteTimeLimitValid(LocalDateTime createdOn) {
		LocalDateTime orderDeleteTimeLimit = createdOn.plusMinutes(20);
		if (now.isAfter(orderDeleteTimeLimit)) {
			logger.info(
					String.format("Order delete is not allowed (createdOn: %s | now: %s) ", createdOn, now));
			throw new OrderDeleteTimeLimitException(
					"El tiempo límite para anular el pedido (20 minutos) ha finalizado");
		}
	}

	@Override
	public void isRequestWithinWorkingHours() {
		// getting now plus 2 hours since host JVM is UTC +00:00
		LocalDateTime localNow = LocalDateTime.now(ZoneOffset.UTC).plusHours(2);
		Instant nowInstant = localNow.toInstant(ZoneOffset.UTC);
		Date date = Date.from(nowInstant);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);

		if (hour < 12 || hour == 23 && minutes > 40) {
			throw new StoreNotOpenException("El horario es de las 12:00h hasta las 23:40 horas.");
		}
	}

	@Override
	public void isCartValid(Order order) {
		if (order.getCart() == null || order.getCart().getOrderItems().isEmpty() || order.getCart().getTotalQuantity() <= 0) {
			throw new EmptyCartException("La cesta no puede ser vacía");
		}
	}

	// value of requested change has to be greater than
	// totalCost or totalOfferCost
	@Override
	public void isChangeRequestedValid(Order order) {
		if (order.getOrderDetails().getChangeRequested() != null) {

			if ((order.getCart().getTotalCostOffers() > 0 &&

					order.getOrderDetails().getChangeRequested() < order.getCart().getTotalCostOffers()) ||

					(order.getCart().getTotalCostOffers() == 0 &&

							order.getOrderDetails().getChangeRequested() < order.getCart().getTotalCost())) {

				throw new InvalidChangeRequestedException(
						"El valor del cambio de efectivo solicitado no puede ser menor o igual "
								+ "que el total/total con ofertas.");
			}
		}
	}

	// calculate totalCost or totalCostOffers - changeRequested
	// the result being the change to give back to the client
	@Override
	public void calculatePaymentChange(Order order) {
		if (order.getOrderDetails().getChangeRequested() == null) {
			order.getOrderDetails().setPaymentChange(null);
		} else {

			if (order.getCart().getTotalCostOffers() > 0) {
				order.getOrderDetails().setPaymentChange
						(order.getOrderDetails().getChangeRequested() - order.getCart().getTotalCostOffers());

			} else {
				order.getOrderDetails().setPaymentChange
						(order.getOrderDetails().getChangeRequested() - order.getCart().getTotalCost());
			}
		}
	}

	public void setNow(LocalDateTime now) {
		this.now = now;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}
}