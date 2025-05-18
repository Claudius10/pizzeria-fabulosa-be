package org.pizzeria.fabulosa.web.controller.open;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.common.dao.address.AddressRepository;
import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.web.dto.order.CreatedOrderDTO;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.order.*;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.helpers.TestUtils.getResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
class AnonControllerAnonOrderTests {

	@Container
	@ServiceConnection
	private final static MariaDBContainer db = new MariaDBContainer<>(DockerImageName.parse("mariadb:latest"));

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AddressRepository addressRepository;

	@Test
	void givenAnonOrderPostApiCall_whenAllOk_thenReturnOrder() throws Exception {
		// Act

		// post api call to create anon order
		MockHttpServletResponse response = mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_ORDER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(anonOrderStub(
								"customerName",
								111222333,
								"customerEmail@gmail.com",
								"Street",
								5,
								"15",
								"E",
								null,
								"ASAP",
								"Cash",
								null,
								false))))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		CreatedOrderDTO order = objectMapper.readValue(response.getContentAsString(), CreatedOrderDTO.class);
		assertThat(order.customer().name()).isEqualTo("customerName");
	}

	@Test
	void givenAnonOrderPostApiCall_whenAddressExists_thenUseExistingAddressAndReturnOrder() throws Exception {

		// Arrange

		// create address object
		Address address = Address.builder()
				.withStreet("Street")
				.withNumber(5)
				.withDetails("Floor Door")
				.build();

		addressRepository.save(address);

		// Act

		// post api call to create anon order
		MockHttpServletResponse response = mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_ORDER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(anonOrderStub(
								"customerName",
								111222333,
								"customerEmail@gmail.com",
								"Street",
								5,
								"Floor",
								"Door",
								null,
								"ASAP",
								"Cash",
								null,
								false))))
				.andReturn().getResponse();
		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		long addressCount = addressRepository.count();
		assertThat(addressCount).isEqualTo(1);
		CreatedOrderDTO order = objectMapper.readValue(response.getContentAsString(), CreatedOrderDTO.class);
		assertThat(order.customer().name()).isEqualTo("customerName");
	}

	@Test
	void givenAnonOrderPostApiCall_whenInvalidCustomerName_thenThrowException() throws Exception {
		// Act

		// post api call to create anon order
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_ORDER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(anonOrderStub(
								"·$dfsaf3",
								111222333,
								"customerEmail@gmail.com",
								"Street",
								5,
								"Floor",
								"Door",
								null,
								"ASAP",
								"Cash",
								null,
								false))))
				// Assert

				.andExpect(result -> {
							assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
							MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
							assert exception != null;
							List<FieldError> errors = exception.getBindingResult().getFieldErrors("customer.name");
							assertThat(errors.getFirst().getDefaultMessage()).isEqualTo(ValidationResponses.NAME_INVALID);
						}
				);
	}

	@Test
	void givenAnonOrderPostApiCall_whenInvalidCustomerNumber_thenThrowException() throws Exception {
		// Act

		// post api call to create anon order
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_ORDER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(anonOrderStub(
								"customerName",
								1,
								"customerEmail@gmail.com",
								"Street",
								5,
								"Floor",
								"Door",
								null,
								"ASAP",
								"Cash",
								null,
								false))))
				// Assert

				.andExpect(result -> {
							assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
							MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
							assert exception != null;
							List<FieldError> errors = exception.getBindingResult().getFieldErrors("customer.contactNumber");
							assertThat(errors.getFirst().getDefaultMessage()).isEqualTo(ValidationResponses.NUMBER_INVALID);
						}
				);
	}

	@Test
	void givenAnonOrderPostApiCall_whenInvalidCustomerEmail_thenThrowException() throws Exception {
		// Act

		// post api call to create anon order
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_ORDER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(anonOrderStub(
								"customerName",
								111222333,
								"dsajn$·~#",
								"Street",
								5,
								"Floor",
								"Door",
								null,
								"ASAP",
								"Cash",
								null,
								false))))
				// Assert

				.andExpect(result -> {
							assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
							MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
							assert exception != null;
							List<FieldError> errors = exception.getBindingResult().getFieldErrors("customer.email");
							assertThat(errors.getFirst().getDefaultMessage()).isEqualTo(ValidationResponses.EMAIL_INVALID);
						}
				);
	}

	@Test
	void givenAnonOrderPostApiCall_whenInvalidAddressStreet_thenThrowException() throws Exception {
		// Act

		// post api call to create anon order
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_ORDER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(anonOrderStub(
								"customerName",
								111222333,
								"anonCustomerEmail@gmail.com",
								"%$·%·",
								5,
								"Floor",
								"Door",
								null,
								"ASAP",
								"Cash",
								null,
								false))))
				// Assert

				.andExpect(result -> {
							assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
							MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
							assert exception != null;
							List<FieldError> errors = exception.getBindingResult().getFieldErrors("address.street");
							assertThat(errors.getFirst().getDefaultMessage()).isEqualTo(ValidationResponses.ADDRESS_STREET);
						}
				);
	}

	@Test
	void givenAnonOrderPostApiCall_whenInvalidAddressStreetNumber_thenThrowException() throws Exception {
		// Act

		// post api call to create anon order
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_ORDER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(anonOrderStub(
								"customerName",
								111222333,
								"anonCustomerEmail@gmail.com",
								"Street",
								512313123,
								"Floor",
								"Door",
								null,
								"ASAP",
								"Cash",
								null,
								false))))
				// Assert

				.andExpect(result -> {
							assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
							MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
							assert exception != null;
							List<FieldError> errors = exception.getBindingResult().getFieldErrors("address.number");
							assertThat(errors.getFirst().getDefaultMessage()).isEqualTo(ValidationResponses.ADDRESS_STREET_NUMBER);
						}
				);
	}

	@Test
	void givenAnonOrderPostApiCall_whenInvalidAddressFloor_thenThrowException() throws Exception {
		// Act

		// post api call to create anon order
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_ORDER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(anonOrderStub(
								"customerName",
								111222333,
								"anonCustomerEmail@gmail.com",
								"Street",
								14,
								"Floor&%$",
								"Door",
								null,
								"ASAP",
								"Cash",
								null,
								false))))
				// Assert

				.andExpect(result -> {
							assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
							MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
							assert exception != null;
							List<FieldError> errors = exception.getBindingResult().getFieldErrors("address.details");
							assertThat(errors.getFirst().getDefaultMessage()).isEqualTo(ValidationResponses.ADDRESS_DETAILS);
						}
				);
	}

	@Test
	void givenAnonOrderPostApiCall_whenInvalidDeliveryHour_thenThrowException() throws Exception {
		// Act

		// post api call to create anon order
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_ORDER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(anonOrderStub(
								"customerName",
								111222333,
								"anonCustomerEmail@gmail.com",
								"Street",
								14,
								"Floor",
								"Door",
								null,
								null,
								"Cash",
								null,
								false))))
				// Assert

				.andExpect(result -> {
							assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
							MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
							assert exception != null;
							List<FieldError> errors = exception.getBindingResult().getFieldErrors("orderDetails.deliveryTime");
							assertThat(errors.getFirst().getDefaultMessage()).isEqualTo(ValidationResponses.ORDER_DETAILS_DELIVERY_HOUR);
						}
				);
	}

	@Test
	void givenAnonOrderPostApiCall_whenInvalidPaymentType_thenThrowException() throws Exception {
		// Act

		// post api call to create anon order
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_ORDER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(anonOrderStub(
								"customerName",
								111222333,
								"anonCustomerEmail@gmail.com",
								"Street",
								14,
								"Floor",
								"Door",
								null,
								"ASAP",
								null,
								null,
								false))))
				// Assert

				.andExpect(result -> {
							assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
							MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
							assert exception != null;
							List<FieldError> errors = exception.getBindingResult().getFieldErrors("orderDetails.paymentMethod");
							assertThat(errors.getFirst().getDefaultMessage()).isEqualTo(ValidationResponses.ORDER_DETAILS_PAYMENT);
						}
				);
	}

	@Test
	void givenAnonOrderPostApiCall_whenInvalidChangeRequest_thenThrowException() throws Exception {
		// Act

		// post api call to create anon order
		MockHttpServletResponse response = mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_ORDER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(anonOrderStub(
								"customerName",
								111222333,
								"anonCustomerEmail@gmail.com",
								"Street",
								14,
								"Floor",
								"Door",
								10D,
								"ASAP",
								"Cash",
								null,
								false))))
				.andReturn()
				.getResponse();


		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getApiError().getCause()).isEqualTo(ValidationResponses.ORDER_DETAILS_BILL);
	}

	@Test
	void givenAnonOrderPostApiCall_whenInvalidComment_thenThrowException() throws Exception {
		// Act

		// post api call to create anon order
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_ORDER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(anonOrderStub(
								"customerName",
								111222333,
								"anonCustomerEmail@gmail.com",
								"Street",
								14,
								"Floor",
								"Door",
								null,
								"ASAP",
								"Cash",
								"%$·%·$",
								false))))
				// Assert

				.andExpect(result -> {
							assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
							MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
							assert exception != null;
							List<FieldError> errors = exception.getBindingResult().getFieldErrors("orderDetails.comment");
							assertThat(errors.getFirst().getDefaultMessage()).isEqualTo(ValidationResponses.ORDER_DETAILS_COMMENT);
						}
				);
	}

	@Test
	void givenAnonOrderPostApiCall_whenCartIsEmpty_thenThrowException() throws Exception {
		// Act

		// post api call to create anon order
		MockHttpServletResponse response = mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_ORDER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(anonOrderStub(
								"customerName",
								111222333,
								"anonCustomerEmail@gmail.com",
								"Street",
								14,
								"Floor",
								"Door",
								null,
								"ASAP",
								"Cash",
								null,
								true))))
				.andReturn()
				.getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getApiError().getCause()).isEqualTo(ValidationResponses.CART_IS_EMPTY);
	}

	NewAnonOrderDTO anonOrderStub(String customerName, int customerNumber, String customerEmail, String street,
								  int streetNumber, String floor, String door, Double changeRequested,
								  String deliveryHour, String paymentType, String comment,
								  boolean emptyCart) {

		CartDTO cartStub = new CartDTO(
				1,
				14.75D,
				0D,
				List.of(new CartItemDTO(
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