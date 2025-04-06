package org.pizzeria.fabulosa.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pizzeria.fabulosa.configs.web.security.utils.SecurityCookieUtils;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.cart.CartItem;
import org.pizzeria.fabulosa.entity.order.OrderDetails;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.repos.user.UserRepository;
import org.pizzeria.fabulosa.services.address.AddressService;
import org.pizzeria.fabulosa.services.order.OrderService;
import org.pizzeria.fabulosa.services.user.UserService;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.order.dto.*;
import org.pizzeria.fabulosa.web.dto.user.dto.RegisterDTO;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TestUtils {


	public static Response getResponse(MockHttpServletResponse response, ObjectMapper mapper) throws JsonProcessingException, UnsupportedEncodingException {
		return mapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), Response.class);
	}

	public static OrderDTO findOrderViaAPI(Long orderId, long userId, String validAccessToken, MockMvc mockMvc,
										   ObjectMapper objectMapper) throws Exception {
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

	public static OrderDTO createUserOrderViaAPI(int minutesInThePast, long userId, long addressId, String validAccessToken,
												 MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
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
		return findOrderViaAPI(orderId, userId, validAccessToken, mockMvc, objectMapper);
	}

	public static Long createUserViaAPI(MockMvc mockMvc, ObjectMapper objectMapper, UserRepository userRepository) throws Exception {
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
		return user.get().getId();
	}

	public static Long createAddress(String streetName, int streetNumber, AddressService addressService) {
		return addressService.create(Address.builder()
				.withStreet(streetName)
				.withNumber(streetNumber)
				.build());
	}

	public static CreatedOrderDTO createUserOrder(OrderService orderService, User user, Long addressId) {
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

		return orderService.createUserOrder(user.getId(), new NewUserOrderDTO(addressId, orderDetails, cart));
	}

	public static User createUser(UserService userService) {

		RegisterDTO registerDTO = new RegisterDTO(
				"Tester",
				"Tester@gmail.com",
				"Tester@gmail.com",
				123456789,
				"Password1",
				"Password1");

		Long userId = userService.createUser(registerDTO);
		return userService.findUserById(userId);
	}

	public static User createUser(UserService userService, RegisterDTO registerDTO) {
		Long userId = userService.createUser(registerDTO);
		return userService.findUserById(userId);
	}

	public static Long createUser(String email, MockMvc mockMvc, ObjectMapper objectMapper, UserRepository userRepository) throws Exception {

		mockMvc.perform(post(
				ApiRoutes.BASE
						+ ApiRoutes.V1
						+ ApiRoutes.ANON_BASE
						+ ApiRoutes.ANON_REGISTER)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new RegisterDTO(
						"Tester",
						email,
						email,
						123456789,
						"Password1",
						"Password1")
				)));

		Optional<User> user = userRepository.findUserByEmailWithRoles(email);
		assertThat(user.isPresent()).isTrue();
		return user.get().getId();
	}

	public static NewAnonOrderDTO anonOrderStub(String customerName, int customerNumber, String customerEmail, String street,
												int streetNumber, String floor, String door, Double changeRequested,
												String deliveryHour, String paymentType, String comment,
												boolean emptyCart) {
		Cart cartStub = new Cart.Builder()
				.withCartItems(List.of(CartItem.builder()
						.withQuantity(1)
						.withPrice(14.75)
						.build()))
				.withTotalQuantity(1)
				.withTotalCost(14.75)
				.withTotalCostOffers(0D)
				.build();

		if (emptyCart) {
			cartStub = new Cart.Builder().withEmptyItemList().build();
		}

		return new NewAnonOrderDTO(
				new CustomerDTO(
						customerName,
						customerNumber,
						customerEmail),
				Address.builder()
						.withStreet(street)
						.withNumber(streetNumber)
						.withDetails(floor + " " + door)
						.build(),
				OrderDetails.builder()
						.withDeliveryTime(deliveryHour)
						.withPaymentMethod(paymentType)
						.withBillToChange(changeRequested)
						.withComment(comment)
						.build(),
				cartStub
		);
	}

	public static void createUserTestSubjectViaAPI(RegisterDTO registerDTO, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ANON_REGISTER)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerDTO)))
				.andExpect(status().isCreated());
	}
}