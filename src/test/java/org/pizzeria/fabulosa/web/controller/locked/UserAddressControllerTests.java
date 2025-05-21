package org.pizzeria.fabulosa.web.controller.locked;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.common.dao.address.AddressRepository;
import org.pizzeria.fabulosa.common.dao.user.UserRepository;
import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.common.entity.role.Role;
import org.pizzeria.fabulosa.helpers.TestHelperService;
import org.pizzeria.fabulosa.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.security.utils.SecurityCookies;
import org.pizzeria.fabulosa.web.dto.api.ResponseDTO;
import org.pizzeria.fabulosa.web.service.user.UserAddressService;
import org.pizzeria.fabulosa.web.util.constant.ApiResponses;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.pizzeria.fabulosa.web.util.constant.SecurityResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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
import java.util.Optional;
import java.util.Set;

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
public class UserAddressControllerTests {

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
	private UserRepository userRepository;

	@Autowired
	private TestHelperService testHelperService;

	@Autowired
	private UserAddressService userAddressService;

	@Autowired
	private AddressRepository addressRepository;

	@Test
	void givenCreateAddressPostApiCall_thenCreateAddressAndReturnCreated() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create access token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// create address object
		Address address = Address.builder()
				.withStreet("Street")
				.withDetails("Gate")
				.withNumber(1)
				.build();

		// Act

		// post api call to add address to user
		MockHttpServletResponse response =
				mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(address))
								.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
						.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

		assertThat(userRepository.count()).isEqualTo(1);
		assertThat(addressRepository.count()).isEqualTo(1);

		Set<Address> userAddressList = userAddressService.findUserAddressListById(userId);
		assertThat(userAddressList).hasSize(1);
	}

	@Test
	void givenCreateAddressPostApiCall_whenAddressExists_thenAddExistingAddress() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create access token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// create address object
		Address address = Address.builder()
				.withStreet("Street")
				.withDetails("Gate")
				.withNumber(1)
				.build();

		addressRepository.save(address);

		// Act

		// post api call to add address to user
		MockHttpServletResponse response =
				mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(address))
								.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
						.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

		assertThat(userRepository.count()).isEqualTo(1);
		assertThat(addressRepository.count()).isEqualTo(1);

		Set<Address> userAddressList = userAddressService.findUserAddressListById(userId);
		assertThat(userAddressList).hasSize(1);
		assertThat(userAddressList.iterator().next()).isEqualTo(address);
	}

	@Test
	void givenCreateAddressPostApiCall_whenUserNotFound_thenReturnUnauthorizedWithMessage() throws Exception {
		// Arrange

		// create access token
		Long userId = 43543L;
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);
		// create address object
		Address address = Address.builder()
				.withStreet("Street")
				.withNumber(1)
				.build();

		// Act

		// post api call to create address
		MockHttpServletResponse response = mockMvc.perform(post(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE +
								ApiRoutes.USER_ID +
								ApiRoutes.USER_ADDRESS,
						userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(address))
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		ResponseDTO responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getApiError().getCause()).isEqualTo(ApiResponses.USER_NOT_FOUND);
	}

	@Test
	void givenCreateAddressPostApiCall_whenMaxAddressSize_thenReturnBadRequestStatusWithMessage() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create access token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// post api call to add address to user
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Address.builder()
						.withStreet("Street")
						.withNumber(1)
						.build()))
				.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)));

		// post api call to add address to user
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Address.builder()
						.withStreet("Street")
						.withNumber(2)
						.build()))
				.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)));

		// post api call to add address to user
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Address.builder()
						.withStreet("Street")
						.withNumber(3)
						.build()))
				.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)));

		// Act

		// post api call to add address to user
		MockHttpServletResponse response = mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Address.builder()
								.withStreet("Street")
								.withNumber(4)
								.build()))
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

		assertThat(addressRepository.count()).isEqualTo(3);

		Set<Address> userAddressList = userAddressService.findUserAddressListById(userId);
		assertThat(userAddressList).hasSize(3);

		ResponseDTO responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getApiError().getCause()).isEqualTo(ApiResponses.ADDRESS_MAX_SIZE);
	}

	@Test
	void givenUserAddressListGetApiCall_thenReturnUserAddressList() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create access token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// post api call to add address to user
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Address.builder()
						.withStreet("Street")
						.withNumber(1)
						.build()))
				.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)));

		// Act

		// get api call to find user address list
		MockHttpServletResponse response = mockMvc.perform(get(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(userRepository.count()).isEqualTo(1);
		assertThat(addressRepository.count()).isEqualTo(1);

		List<Address> userAddressList = objectMapper.readValue(response.getContentAsString(), List.class);
		assertThat(userAddressList).hasSize(1);
	}

	@Test
	void givenUserAddressListGetApiCall_whenAddressListIsEmpty_thenReturnNoContent() throws Exception {
		// Arrange

		// create access token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), 3L);

		// Act

		// get api call to find user address list
		MockHttpServletResponse response = mockMvc.perform(get(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ADDRESS, 3)
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void givenDeleteUserAddressApiCall_thenDeleteUserAddress() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create address object
		Address address = Address.builder()
				.withStreet("Street")
				.withNumber(1)
				.build();

		// create access token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// post api call to add address to user
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(address))
				.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)));

		// confirm address was added
		Set<Address> userAddressList = userAddressService.findUserAddressListById(userId);
		assertThat(userAddressList).hasSize(1);

		// find created address
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
		Optional<Address> dbAddress = addressRepository.findOne(Example.of(address, matcher));

		// Act

		// delete api call to delete user address
		MockHttpServletResponse response =
				mockMvc.perform(delete(
								ApiRoutes.BASE +
										ApiRoutes.V1 +
										ApiRoutes.USER_BASE +
										ApiRoutes.USER_ID +
										ApiRoutes.USER_ADDRESS +
										ApiRoutes.USER_ADDRESS_ID, userId,
								dbAddress.get().getId())
								.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
						.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		Set<Address> userAddressListAfterDelete = userAddressService.findUserAddressListById(userId);
		assertThat(userAddressListAfterDelete).isEmpty();
	}

	@Test
	void givenDeleteUserAddressApiCall_whenUserAddressNotFound_thenReturnBadRequestWithMessage() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = testHelperService.createUser("Tester@gmail.com");

		// create access token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// Act

		// delete api call to delete user address
		MockHttpServletResponse response = mockMvc.perform(delete(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE +
								ApiRoutes.USER_ID +
								ApiRoutes.USER_ADDRESS +
								ApiRoutes.USER_ADDRESS_ID,
						userId,
						2L)
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		ResponseDTO responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getApiError().getCause()).isEqualTo(ApiResponses.ADDRESS_NOT_FOUND);
	}

	@Test
	void givenDeleteUserAddressApiCall_whenUserNotFound_thenReturnUnauthorized() throws Exception {
		// Arrange

		// create access token
		String accessToken = JWTTokenManager.generateAccessToken("Tester@gmail.com", List.of(new Role("USER")), 2L);

		// Act

		// delete api call to delete user address
		MockHttpServletResponse response = mockMvc.perform(delete(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE +
								ApiRoutes.USER_ID +
								ApiRoutes.USER_ADDRESS +
								ApiRoutes.USER_ADDRESS_ID,
						99L,
						2L)
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		ResponseDTO responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getApiError().getCause()).isEqualTo(SecurityResponses.USER_ID_NO_MATCH);
	}
}
