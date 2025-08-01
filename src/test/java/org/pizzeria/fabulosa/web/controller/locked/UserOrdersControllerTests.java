package org.pizzeria.fabulosa.web.controller.locked;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.common.entity.projection.OrderProjection;
import org.pizzeria.fabulosa.common.entity.role.Role;
import org.pizzeria.fabulosa.helpers.TestHelperService;
import org.pizzeria.fabulosa.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.security.utils.SecurityCookies;
import org.pizzeria.fabulosa.web.dto.api.ResponseDTO;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.helpers.TestUtils.getResponse;
import static org.pizzeria.fabulosa.web.util.constant.SecurityConstants.ACCESS_TOKEN;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
@Sql(scripts = "file:src/test/resources/db/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(transactionMode = ISOLATED))
@Sql(scripts = "file:src/test/resources/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = ISOLATED))
class UserOrdersControllerTests {

	@Container
	@ServiceConnection
	private final static MariaDBContainer db = new MariaDBContainer<>(DockerImageName.parse("mariadb:latest"));

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JWTTokenManager JWTTokenManager;

	@Autowired
	private TestHelperService testHelperService;


	@Test
	void givenPostApiCallToCreateOrder_thenReturnCreatedAndDTO() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create address in database
		Long addressId = testHelperService.createAddress("Test", 1);

		// create JWT token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// create DTO object
		CartDTO cart = new CartDTO(
				1,
				18.30D,
				0D,
				List.of(new CartItemDTO(
						null,
						"pizza",
						13.30D,
						1,
						Map.of("es", "Cuatro Quesos", "en", "Cuatro Quesos"),
						Map.of(
								"es", List.of("Salsa de Tomate", "Mozzarella 100%", "Parmesano", "Emmental", "Queso Azul"),
								"en", List.of("Tomato Sauce", "100% Mozzarella", "Parmesan Cheese", "Emmental Cheese", "Blue Cheese")
						),
						Map.of("m", Map.of("es", "Mediana", "en", "Medium"), "l", Map.of(), "s", Map.of())
				))
		);

		OrderDetailsDTO orderDetails = new OrderDetailsDTO(
				"ASAP",
				"Card",
				null,
				null,
				false,
				0D
		);

		NewUserOrderDTO newUserOrderDTO = new NewUserOrderDTO(addressId, orderDetails, cart);

		// post api call to create user order
		MockHttpServletResponse response = mockMvc.perform(post(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newUserOrderDTO))
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		CreatedOrderDTO createdOrderDTO = objectMapper.readValue(response.getContentAsString(), CreatedOrderDTO.class);
		assertThat(createdOrderDTO.customer().name()).isEqualTo("Tester");
		assertThat(createdOrderDTO.customer().email()).isEqualTo("Tester@gmail.com");
		assertThat(createdOrderDTO.cart().cartItems().size()).isGreaterThan(0);
	}

	@Test
	void givenPostApiCallToCreateOrder_whenCartIsEmpty_thenReturnBadRequestWithMessage() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create address in database
		Long addressId = testHelperService.createAddress("Test", 1);

		// create JWT token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		CartDTO cart = new CartDTO(
				0,
				0D,
				0D,
				List.of()
		);

		OrderDetailsDTO orderDetails = new OrderDetailsDTO(
				"ASAP",
				"Card",
				null,
				null,
				false,
				0D
		);

		NewUserOrderDTO newUserOrderDTO = new NewUserOrderDTO(addressId, orderDetails, cart);

		// post api call to create user order
		MockHttpServletResponse response = mockMvc.perform(post(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newUserOrderDTO))
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		ResponseDTO responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getApiError().getCause()).isEqualTo(ValidationResponses.CART_IS_EMPTY);
	}

	@Test
	void givenGetApiCallToFindOrder_thenReturnOrder() throws Exception {

		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create address in database
		Long addressId = testHelperService.createAddress("Test", 1);

		// create JWT token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// create DTO object
		CartDTO cart = new CartDTO(
				1,
				18.30D,
				0D,
				List.of(new CartItemDTO(
						null,
						"pizza",
						13.30D,
						1,
						Map.of("es", "Cuatro Quesos", "en", "Cuatro Quesos"),
						Map.of(
								"es", List.of("Salsa de Tomate", "Mozzarella 100%", "Parmesano", "Emmental", "Queso Azul"),
								"en", List.of("Tomato Sauce", "100% Mozzarella", "Parmesan Cheese", "Emmental Cheese", "Blue Cheese")
						),
						Map.of("m", Map.of("es", "Mediana", "en", "Medium"), "l", Map.of(), "s", Map.of())
				))
		);

		OrderDetailsDTO orderDetails = new OrderDetailsDTO(
				"ASAP",
				"Card",
				null,
				null,
				false,
				0D
		);

		NewUserOrderDTO newUserOrderDTO = new NewUserOrderDTO(addressId, orderDetails, cart);

		// post api call to create user order
		MockHttpServletResponse response = mockMvc.perform(post(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newUserOrderDTO))
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		CreatedOrderDTO createdOrder = objectMapper.readValue(response.getContentAsString(), CreatedOrderDTO.class);

		// Act

		// get api call to find user order
		MockHttpServletResponse getResponse = mockMvc.perform(get(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER
								+ ApiRoutes.ORDER_ID,
						userId, createdOrder.id())
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(getResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		OrderDTO order = objectMapper.readValue(getResponse.getContentAsString(), OrderDTO.class);
		assertThat(order.id()).isEqualTo(createdOrder.id());
	}

	@Test
	void givenGetApiCallToFindOrder_whenOrderNotFound_thenReturnNoContent() throws Exception {

		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create JWT token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// Act

		// get api call to find user order
		MockHttpServletResponse response = mockMvc.perform(get(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER
								+ ApiRoutes.ORDER_ID, userId, 99)
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void givenOrderDelete_whenWithinTimeLimit_thenReturnDeletedOrderId() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create address in database
		Long addressId = testHelperService.createAddress("Test", 1);

		// create JWT token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// create user order
		int minutesInThePast = 0;
		OrderProjection order = testHelperService.createOrder(userId, addressId, minutesInThePast);

		// Act

		// delete api call to delete order
		MockHttpServletResponse response = mockMvc.perform(delete(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER
								+ ApiRoutes.ORDER_ID, userId, order.getId())
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 30, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		Long id = objectMapper.readValue(response.getContentAsString(), Long.class);
		assertThat(id).isEqualTo(order.getId());
	}

	@Test
	void givenOrderDelete_whenTimeLimitPassed_thenReturnBadRequestWithMessage() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create address in database
		Long addressId = testHelperService.createAddress("Test", 1);

		// create JWT token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// create user order
		int minutesInThePast = 21;
		OrderProjection order = testHelperService.createOrder(userId, addressId, minutesInThePast);

		// Act

		// delete api call to delete order
		MockHttpServletResponse response = mockMvc.perform(delete(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER
								+ ApiRoutes.ORDER_ID, userId, order.getId())
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 30, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		ResponseDTO responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getApiError().getCause()).isEqualTo(ValidationResponses.ORDER_DELETE_TIME_ERROR);
	}

	@Test
	void givenOrderDelete_whenOrderNotFound_thenReturnNoContent() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create JWT token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// Act

		// delete api call to delete order
		MockHttpServletResponse response = mockMvc.perform(delete(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER
								+ ApiRoutes.ORDER_ID, userId, 995678)
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 30, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void givenGetUserOrderSummary_thenReturnUserOrderSummary() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create address in database
		Long addressId = testHelperService.createAddress("Test", 1);

		// create JWT token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// create user order
		int minutesInThePast = 0;
		testHelperService.createOrder(userId, addressId, minutesInThePast);

		int pageSize = 5;
		int pageNumber = 0;

		// Act

		// get api call to get OrderSummary
		MockHttpServletResponse response = mockMvc.perform(get(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER
								+ ApiRoutes.ORDER_SUMMARY + "?pageNumber={pN}&pageSize={pS}", userId, pageNumber, pageSize)
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 30, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		OrderSummaryListDTO orderList = objectMapper.readValue(response.getContentAsString(), OrderSummaryListDTO.class);
		assertThat(orderList).isNotNull();
		assertThat(orderList.orderList().size()).isGreaterThan(0);
		assertThat(orderList.totalElements()).isGreaterThan(0);
	}

	@Test
	void givenGetUserOrderSummary_whenNoOrders_thenReturnEmptyOrderSummaryList() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create JWT token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		int pageSize = 1;
		int pageNumber = 0;

		// Act

		// get api call to get OrderSummary
		MockHttpServletResponse response = mockMvc.perform(get(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER
								+ ApiRoutes.ORDER_SUMMARY + "?pageNumber={pN}&pageSize={pS}", userId, pageNumber, pageSize)
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 30, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
//		Response responseObj = getResponse(response, objectMapper);
//		OrderSummaryListDTO orderList = objectMapper.convertValue(responseObj.getPayload(), OrderSummaryListDTO.class);
//		assertThat(orderList).isNotNull();
//		assertThat(orderList.orderList().size()).isZero();
//		assertThat(orderList.totalElements()).isZero();
	}

	@Test
	void givenGetUserOrderSummary_whenUserNotFound_thenReturnNoContent() throws Exception {
		// Arrange

		// create JWT token
		Long userId = 985643L;
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		int pageSize = 1;
		int pageNumber = 0;

		// Act

		// get api call to get OrderSummary
		MockHttpServletResponse response = mockMvc.perform(get(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER
								+ ApiRoutes.ORDER_SUMMARY + "?pageNumber={pN}&pageSize={pS}", userId, pageNumber, pageSize)
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 30, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}