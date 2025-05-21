package org.pizzeria.fabulosa.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pizzeria.fabulosa.web.dto.api.ResponseDTO;
import org.pizzeria.fabulosa.web.dto.order.*;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class TestUtils {

	public static ResponseDTO getResponse(MockHttpServletResponse response, ObjectMapper mapper) throws JsonProcessingException, UnsupportedEncodingException {
		return mapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), ResponseDTO.class);
	}

	public static NewAnonOrderDTO anonOrderStub(String customerName, int customerNumber, String customerEmail, String street,
												int streetNumber, String floor, String door, Double changeRequested,
												String deliveryHour, String paymentType, String comment,
												boolean emptyCart) {

		CartDTO cartStub = new CartDTO(
				1,
				14.75D,
				0D,
				List.of(new CartItemDTO(
						null,
						"pizza",
						14.75D,
						1,
						Map.of("es", "Cuatro Quesos", "en", "Cuatro Quesos"),
						Map.of(
								"es", List.of("Salsa de Tomate", "Mozzarella 100%", "Parmesano", "Emmental", "Queso Azul"),
								"en", List.of("Tomato Sauce", "100% Mozzarella", "Parmesan Cheese", "Emmental Cheese", "Blue Cheese")
						),
						Map.of("m", Map.of("es", "Mediana", "en", "Medium"), "l", Map.of(), "s", Map.of())
				))
		);

		if (emptyCart) {
			cartStub = new CartDTO(
					0,
					0D,
					0D,
					List.of()
			);
		}

		return new NewAnonOrderDTO(
				new CustomerDTO(
						customerName,
						customerNumber,
						customerEmail),
				new AddressDTO(null, street, streetNumber, floor + " " + door),
				new OrderDetailsDTO(deliveryHour, paymentType, changeRequested, comment, false, 0D),
				cartStub
		);
	}
}