package org.pizzeria.fabulosa.controller.locked;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.configs.web.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.configs.web.security.utils.SecurityCookieUtils;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.cart.CartItem;
import org.pizzeria.fabulosa.entity.order.OrderDetails;
import org.pizzeria.fabulosa.entity.role.Role;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.repos.address.AddressRepository;
import org.pizzeria.fabulosa.repos.order.OrderRepository;
import org.pizzeria.fabulosa.repos.user.UserRepository;
import org.pizzeria.fabulosa.utils.Constants;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.constants.ValidationResponses;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.order.dto.CreatedOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.NewUserOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.OrderDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.OrderSummaryListDTO;
import org.pizzeria.fabulosa.web.dto.user.dto.RegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.utils.TestUtils.getResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Sql(scripts = {"file:src/test/resources/db/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@DirtiesContext
class UserOrdersControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JWTTokenManager JWTTokenManager;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;

	@AfterEach
	void cleanUp() {
		orderRepository.deleteAll();
		userRepository.deleteAll();
		addressRepository.deleteAll();
	}

	@Test
	void givenPostApiCallToCreateOrder_thenCreateOrder() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser();

		// create address in database
		Long addressId = createAddressTestSubject("Test", 1);

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// create DTO object
		Cart cart = new Cart.Builder()
				.withCartItems(List.of(CartItem.builder()
						.withQuantity(1)
						.withPrice(18.30)
						.build()))
				.withTotalQuantity(1)
				.withTotalCost(18.30)
				.build();

		OrderDetails orderDetails = OrderDetails.builder()
				.withDeliveryTime("ASAP")
				.withPaymentMethod("Card")
				.build();

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
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@Test
	void givenPostApiCallToCreateOrder_whenCartIsEmpty_thenReturnBadRequestWithMessage() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser();

		// create address in database
		Long addressId = createAddressTestSubject("Test", 1);

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		OrderDetails orderDetails = OrderDetails.builder()
				.withDeliveryTime("ASAP")
				.withPaymentMethod("Card")
				.build();

		NewUserOrderDTO newUserOrderDTO = new NewUserOrderDTO(addressId, orderDetails, null);

		// post api call to create user order
		MockHttpServletResponse response = mockMvc.perform(post(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newUserOrderDTO))
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(responseObj.getError().getCause()).isEqualTo(ValidationResponses.CART_IS_EMPTY);
	}

	@Test
	void givenGetApiCallToFindOrder_thenReturnOrder() throws Exception {

		// Arrange

		// post api call to register new user in database
		Long userId = createUser();

		// create address in database
		Long addressId = createAddressTestSubject("Test", 1);

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// create DTO object
		Cart cart = new Cart.Builder()
				.withCartItems(List.of(CartItem.builder()
						.withQuantity(1)
						.withPrice(18.30)
						.build()))
				.withTotalQuantity(1)
				.withTotalCost(18.30)
				.build();

		OrderDetails orderDetails = OrderDetails.builder()
				.withDeliveryTime("ASAP")
				.withPaymentMethod("Card")
				.build();

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
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		Response responseObj = getResponse(response, objectMapper);
		CreatedOrderDTO createdOrder = objectMapper.convertValue(responseObj.getPayload(), CreatedOrderDTO.class);

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
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObjTwo = getResponse(getResponse, objectMapper);

		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(responseObjTwo.getStatus().getCode()).isEqualTo(HttpStatus.OK.value());

		OrderDTO order = objectMapper.convertValue(responseObjTwo.getPayload(), OrderDTO.class);
		assertThat(order.id()).isEqualTo(createdOrder.id());
	}

	@Test
	void givenGetApiCallToFindOrder_whenOrderNotFound_thenReturnAcceptedWithMessage() throws Exception {

		// Arrange

		// post api call to register new user in database
		Long userId = createUser();

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// Act

		// get api call to find user order
		MockHttpServletResponse response = mockMvc.perform(get(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER
								+ ApiRoutes.ORDER_ID, userId, 99)
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
		assertThat(responseObj.getStatus().getDescription()).isEqualTo(HttpStatus.NO_CONTENT.name());
	}

	@Test
	void givenOrderDelete_whenWithinTimeLimit_thenReturnDeletedOrderId() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser();

		// create address in database
		Long addressId = createAddressTestSubject("Test", 1);

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// create user order
		int minutesInThePast = 0;
		OrderDTO order = createUserOrderTestSubject(minutesInThePast, userId, addressId, accessToken);

		// Act

		// delete api call to delete order
		MockHttpServletResponse response = mockMvc.perform(delete(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER
								+ ApiRoutes.ORDER_ID, userId, order.id())
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 30, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.OK.value());

		Long id = objectMapper.convertValue(responseObj.getPayload(), Long.class);
		assertThat(id).isEqualTo(order.id());
	}

	@Test
	void givenOrderDelete_whenTimeLimitPassed_thenReturnBadRequestWithMessage() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser();

		// create address in database
		Long addressId = createAddressTestSubject("Test", 1);

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// create user order
		int minutesInThePast = 21;
		OrderDTO order = createUserOrderTestSubject(minutesInThePast, userId, addressId, accessToken);

		// Act

		// delete api call to delete order
		MockHttpServletResponse response = mockMvc.perform(delete(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER
								+ ApiRoutes.ORDER_ID, userId, order.id())
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 30, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(responseObj.getError().getCause()).isEqualTo(ValidationResponses.ORDER_DELETE_TIME_ERROR);
	}

	@Test
	void givenOrderDelete_whenOrderNotFound_thenReturnNoContent() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser();

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// Act

		// delete api call to delete order
		MockHttpServletResponse response = mockMvc.perform(delete(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER
								+ ApiRoutes.ORDER_ID, userId, 995678)
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 30, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void givenGetUserOrderSummary_thenReturnUserOrderSummary() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser();

		// create address in database
		Long addressId = createAddressTestSubject("Test", 1);

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// create user order
		int minutesInThePast = 0;
		createUserOrderTestSubject(minutesInThePast, userId, addressId, accessToken);

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
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 30, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.OK.value());
	}

	@Test
	void givenGetUserOrderSummary_whenNoOrders_thenReturnEmptyOrderSummaryList() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser();

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

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
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 30, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.OK.value());
		OrderSummaryListDTO orderList = objectMapper.convertValue(responseObj.getPayload(), OrderSummaryListDTO.class);
		assertThat(orderList).isNotNull();
		assertThat(orderList.orderList()).isEmpty();
	}

	@Test
	void givenGetUserOrderSummary_whenUserNotFound_thenReturnUnauthorizedWithMessage() throws Exception {
		// Arrange

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), 0L);

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
								+ ApiRoutes.ORDER_SUMMARY + "?pageNumber={pN}&pageSize={pS}", 0, pageNumber, pageSize)
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 30, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(responseObj.getError().getCause()).isEqualTo("UsernameNotFoundException");
	}

	OrderDTO findOrder(Long orderId, long userId, String validAccessToken) throws Exception {
		MockHttpServletResponse response = mockMvc.perform(get(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ORDER
								+ ApiRoutes.ORDER_ID, userId, orderId)
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, validAccessToken, 1800, true, false)))
				.andReturn().getResponse();

		Response responseObj = getResponse(response, objectMapper);
		return objectMapper.convertValue(responseObj.getPayload(), OrderDTO.class);
	}

	OrderDTO createUserOrderTestSubject(int minutesInThePast, long userId, long addressId, String validAccessToken) throws Exception {
		Cart cart = new Cart.Builder()
				.withCartItems(List.of(CartItem.builder()
						.withQuantity(1)
						.withPrice(18.30)
						.build()))
				.withTotalQuantity(1)
				.withTotalCost(18.30)
				.build();

		OrderDetails orderDetails = OrderDetails.builder()
				.withDeliveryTime("ASAP")
				.withPaymentMethod("Card")
				.build();

		NewUserOrderDTO newUserOrderDTO = new NewUserOrderDTO(addressId, orderDetails, cart);

		// post api call to create user order
		MockHttpServletResponse response = mockMvc.perform(post(
						"/api/tests/user/{userId}/order?minusMin={minutesInThePast}", userId, minutesInThePast)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newUserOrderDTO))
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, validAccessToken, 1800, true, false)))
				.andReturn().getResponse();

		Long orderId = Long.valueOf(response.getContentAsString());
		return findOrder(orderId, userId, validAccessToken);
	}

	Long createUser() throws Exception {
		mockMvc.perform(post(
				ApiRoutes.BASE
						+ ApiRoutes.V1
						+ ApiRoutes.ANON_BASE
						+ ApiRoutes.ANON_REGISTER)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new RegisterDTO(
						"Tester",
						"Tester@gmail.com",
						"Tester@gmail.com",
						123456789,
						"Password1",
						"Password1")
				)));

		Optional<User> user = userRepository.findUserByEmailWithRoles("Tester@gmail.com");
		assertThat(user.isPresent()).isTrue();
		return user.get().getId();
	}

	Long createAddressTestSubject(String streetName, int streetNumber) {
		return addressRepository.save(
						Address.builder()
								.withStreet(streetName)
								.withNumber(streetNumber)
								.build())
				.getId();
	}
}