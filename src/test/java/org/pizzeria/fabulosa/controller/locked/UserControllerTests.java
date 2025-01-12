package org.pizzeria.fabulosa.controller.locked;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.configs.web.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.configs.web.security.utils.SecurityCookieUtils;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.role.Role;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.repos.address.AddressRepository;
import org.pizzeria.fabulosa.repos.user.UserRepository;
import org.pizzeria.fabulosa.utils.Constants;
import org.pizzeria.fabulosa.web.constants.ApiResponses;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.constants.SecurityResponses;
import org.pizzeria.fabulosa.web.constants.ValidationResponses;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.auth.RegisterDTO;
import org.pizzeria.fabulosa.web.dto.user.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.utils.TestUtils.getResponse;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Sql(scripts = "file:src/test/resources/db/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(transactionMode = ISOLATED))
@Sql(scripts = "file:src/test/resources/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = ISOLATED))
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@DirtiesContext
class UserControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JWTTokenManager JWTTokenManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void givenFindUserGetApiCall_whenUserNotFound_thenReturnNoContent() throws Exception {
		// Arrange

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), 99L);

		// Act

		// get api call to find user
		MockHttpServletResponse response =
				mockMvc.perform(get(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID, 99)
								.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
						.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
		assertThat(responseObj.getStatus().getDescription()).isEqualTo(HttpStatus.NO_CONTENT.name());
	}

	@Test
	void givenFindUserGetApiCall_whenUserFound_thenReturnUser() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester@gmail.com");

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// Act

		MockHttpServletResponse response = mockMvc.perform(get(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID, userId)
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.OK.value());
		UserDTO userDTO = objectMapper.convertValue(responseObj.getPayload(), UserDTO.class);
		assertThat(userDTO.id()).isEqualTo(userId);
	}


	@Test
	void givenCreateAddressPostApiCall_thenCreateAddressAndReturnAccepted() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester@gmail.com");

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

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
								.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
						.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

		assertThat(userRepository.count()).isEqualTo(1);
		assertThat(addressRepository.count()).isEqualTo(1);

		Set<Address> userAddressList = userRepository.findUserAddressListById(userId);
		assertThat(userAddressList).hasSize(1);
	}

	@Test
	void givenCreateAddressPostApiCall_whenUserNotFound_thenReturnUnauthorizedWithMessage() throws Exception {
		// Arrange

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), 2L);
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
						999)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(address))
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getError().getCause()).isEqualTo(SecurityResponses.USER_ID_NO_MATCH);
		assertThat(responseObj.getStatus().getDescription()).isEqualTo(HttpStatus.UNAUTHORIZED.name());
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	void givenCreateAddressPostApiCall_whenMaxAddressSize_thenReturnBadRequestStatusWithMessage() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester@gmail.com");

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// post api call to add address to user
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Address.builder()
						.withStreet("Street")
						.withNumber(1)
						.build()))
				.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)));

		// post api call to add address to user
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Address.builder()
						.withStreet("Street")
						.withNumber(2)
						.build()))
				.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)));

		// post api call to add address to user
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Address.builder()
						.withStreet("Street")
						.withNumber(3)
						.build()))
				.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)));

		// Act

		// post api call to add address to user
		MockHttpServletResponse response = mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Address.builder()
								.withStreet("Street")
								.withNumber(4)
								.build()))
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(addressRepository.count()).isEqualTo(3);

		Set<Address> userAddressList = userRepository.findUserAddressListById(userId);
		assertThat(userAddressList).hasSize(3);

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getError().getCause()).isEqualTo(ApiResponses.ADDRESS_MAX_SIZE);
		assertThat(responseObj.getStatus().getDescription()).isEqualTo(HttpStatus.BAD_REQUEST.name());
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void givenUserAddressListGetApiCall_thenReturnUserAddressList() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester@gmail.com");

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// post api call to add address to user
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Address.builder()
						.withStreet("Street")
						.withNumber(1)
						.build()))
				.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)));

		// Act

		// get api call to find user address list
		MockHttpServletResponse response = mockMvc.perform(get(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(userRepository.count()).isEqualTo(1);
		assertThat(addressRepository.count()).isEqualTo(1);

		Response responseObj = getResponse(response, objectMapper);
		List<Address> userAddressList = objectMapper.convertValue(responseObj.getPayload(), List.class);
		assertThat(userAddressList).hasSize(1);
	}

	@Test
	void givenUserAddressListGetApiCall_whenNotFound_thenReturnAcceptedWithMessage() throws Exception {
		// Arrange

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), 3L);

		// Act

		// get api call to find user address list
		MockHttpServletResponse response = mockMvc.perform(get(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.USER_BASE
								+ ApiRoutes.USER_ID
								+ ApiRoutes.USER_ADDRESS, 3)
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getDescription()).isEqualTo(HttpStatus.NO_CONTENT.name());
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void givenDeleteUserAddressApiCall_thenDeleteUserAddress() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester@gmail.com");

		// create address object
		Address address = Address.builder()
				.withStreet("Street")
				.withNumber(1)
				.build();

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// post api call to add address to user
		mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS, userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(address))
				.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)));

		// confirm address was added
		Set<Address> userAddressList = userRepository.findUserAddressListById(userId);
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
								.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
						.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		Set<Address> userAddressListAfterDelete = userRepository.findUserAddressListById(userId);
		assertThat(userAddressListAfterDelete).isEmpty();
	}

	@Test
	void givenDeleteUserAddressApiCall_whenUserAddressNotFound_thenReturnNoContent() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester@gmail.com");

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

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
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getDescription()).isEqualTo(HttpStatus.NO_CONTENT.name());
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void givenDeleteUserAddressApiCall_whenUserNotFound_thenReturnUnauthorized() throws Exception {
		// Arrange

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), 2L);

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
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert
		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getError().getCause()).isEqualTo(SecurityResponses.USER_ID_NO_MATCH);
		assertThat(responseObj.getStatus().getDescription()).isEqualTo(HttpStatus.UNAUTHORIZED.name());
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	void givenNameUpdatePutApiCall_whenInvalidFormat_thenThrowError() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester@gmail.com");

		// create dto object
		NameChangeDTO nameChangeDTO = new NameChangeDTO("dsa$·", "Password1");

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// Act

		// put api call to update user name
		mockMvc.perform(put(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE +
								ApiRoutes.USER_ID +
								ApiRoutes.USER_NAME,
						userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(nameChangeDTO))
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))

				// Assert

				.andExpect(result -> {
							assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
							MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
							assert exception != null;
							List<FieldError> errors = exception.getBindingResult().getFieldErrors("name");
							assertThat(errors.getFirst().getDefaultMessage()).isEqualTo(ValidationResponses.NAME_INVALID);
						}
				);
	}

	@Test
	void givenNameUpdatePutApiCall_thenUpdateName() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester3@gmail.com");

		// create dto object
		NameChangeDTO nameChangeDTO = new NameChangeDTO("NewUserName", "Password1");

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester3@gmail.com", List.of(new Role("USER")), userId);

		// Act

		// put api call to update user name
		MockHttpServletResponse response = mockMvc.perform(put(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE +
								ApiRoutes.USER_ID +
								ApiRoutes.USER_NAME,
						userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(nameChangeDTO))
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.OK.value());
	}

	@Test
	void givenEmailUpdatePutApiCall_whenInvalidFormat_thenThrowError() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester@gmail.com");

		// create dto object
		EmailChangeDTO emailChangeDTO = new EmailChangeDTO("invalidEmailFormat", "Password1");

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// Act

		// put api call to update user email
		mockMvc.perform(put(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE +
								ApiRoutes.USER_ID +
								ApiRoutes.USER_EMAIL, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(emailChangeDTO))
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))

				// Assert

				.andExpect(result -> {
							assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
							MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
							assert exception != null;
							List<FieldError> errors = exception.getBindingResult().getFieldErrors("email");
							assertThat(errors.getFirst().getDefaultMessage()).isEqualTo(ValidationResponses.EMAIL_INVALID);
						}
				);
	}

	@Test
	void givenEmailUpdatePutApiCall_thenUpdateEmail() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester3@gmail.com");

		// create dto object
		EmailChangeDTO emailChangeDTO = new EmailChangeDTO("validEmailFormat@gmail.com", "Password1");

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester3@gmail.com", List.of(new Role("USER")), userId);

		// Act

		// put api call to update user email
		MockHttpServletResponse response = mockMvc.perform(put(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE +
								ApiRoutes.USER_ID +
								ApiRoutes.USER_EMAIL, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(emailChangeDTO))
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.OK.value());
		User user = userRepository.findUserByEmail(emailChangeDTO.email());
		assertThat(user.getEmail()).isEqualTo(emailChangeDTO.email());
	}

	@Test
	void givenEmailUpdatePutApiCall_whenEmailAlreadyExists_thenReturnBadRequestWithMessage() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester3@gmail.com");
		Long userIdTwo = createUser("Tester4@gmail.com");

		// create dto object
		EmailChangeDTO emailChangeDTO = new EmailChangeDTO("Tester3@gmail.com", "Password1");

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester4@gmail.com", List.of(new Role("USER")), userIdTwo);

		// Act

		// put api call to update user email
		MockHttpServletResponse response = mockMvc.perform(put(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE +
								ApiRoutes.USER_ID +
								ApiRoutes.USER_EMAIL, userIdTwo)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(emailChangeDTO))
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(responseObj.getError().getCause()).isEqualTo(ApiResponses.USER_EMAIL_ALREADY_EXISTS);
	}

	@Test
	void givenContactNumberUpdatePutApiCall_whenInvalidFormat_thenThrowError() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester@gmail.com");

		// create dto object
		ContactNumberChangeDTO contactNumberChangeDTO = new ContactNumberChangeDTO(123, "Password1");

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// Act

		// put api call to update user contact number
		mockMvc.perform(put(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE +
								ApiRoutes.USER_ID +
								ApiRoutes.USER_NUMBER, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(contactNumberChangeDTO))
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))

				// Assert

				.andExpect(result -> {
							assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
							MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
							assert exception != null;
							List<FieldError> errors = exception.getBindingResult().getFieldErrors("contactNumber");
							assertThat(errors.getFirst().getDefaultMessage()).isEqualTo(ValidationResponses.NUMBER_INVALID);
						}
				);
	}

	@Test
	void givenContactNumberUpdatePutApiCall_thenUpdateContactNumber() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester3@gmail.com");

		// create dto object
		ContactNumberChangeDTO contactNumberChangeDTO = new ContactNumberChangeDTO(123456789, "Password1");

		// create access token
		String accessToken = JWTTokenManager.getAccessToken("Tester3@gmail.com", List.of(new Role("USER")), userId);

		// Act

		// put api call to update user contact number
		MockHttpServletResponse response = mockMvc.perform(put(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE +
								ApiRoutes.USER_ID +
								ApiRoutes.USER_NUMBER, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(contactNumberChangeDTO))
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 60, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.OK.value());
		User user = userRepository.findUserByEmail("Tester3@gmail.com");
		assertThat(user.getContactNumber()).isEqualTo(contactNumberChangeDTO.contactNumber());
	}

	@Test
	void givenUpdatePasswordPutApiCall_whenInvalidCurrentPassword_thenThrowBadCredentialsException() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester@gmail.com");

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester@gmail.com", List.of(new Role("USER")), userId);

		// create dto object
		PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO(
				"Password",
				"Password2",
				"Password2");

		// Act

		// put api call to update password
		MockHttpServletResponse response = mockMvc.perform(put(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE +
								ApiRoutes.USER_ID +
								ApiRoutes.USER_PASSWORD, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(passwordChangeDTO))
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 1800, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(responseObj.getError().getCause()).isEqualTo(SecurityResponses.BAD_CREDENTIALS);
	}

	@Test
	void givenUpdatePasswordPutApiCall_thenUpdateUserPassword() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester3@gmail.com");

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester3@gmail.com", List.of(new Role("USER")), userId);

		// create dto object
		PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO(
				"Password1",
				"Password2",
				"Password2");

		// Act

		// put api call to update password
		MockHttpServletResponse response = mockMvc.perform(put(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE +
								ApiRoutes.USER_ID +
								ApiRoutes.USER_PASSWORD, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(passwordChangeDTO))
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 1800, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.OK.value());
		User user = userRepository.findUserByEmail("Tester3@gmail.com");
		assertThat(passwordEncoder.matches(passwordChangeDTO.newPassword(), user.getPassword())).isTrue();
	}

	@Test
	void givenDeleteUserApiCall_whenInvalidCurrentPassword_thenThrowBadCredentialsException() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester3@gmail.com");

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester3@gmail.com", List.of(new Role("USER")), userId);

		// password
		String password = "WrongPassword";

		// Act

		// delete api call to delete user
		MockHttpServletResponse response = mockMvc.perform(delete(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE + "?id={userId}&password={password}", userId, password)
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 1800, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(responseObj.getError().getCause()).isEqualTo(SecurityResponses.BAD_CREDENTIALS);
	}

	@Test
	void givenDeleteUserApiCall_thenDeleteUser() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester3@gmail.com");

		Optional<User> user = userRepository.findUserByEmailWithRoles("Tester3@gmail.com");
		assertThat(user).isPresent();

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken("Tester3@gmail.com", List.of(new Role("USER")), userId);

		// create dto object
		String password = "Password1";

		// Act

		// put api call to delete the user
		MockHttpServletResponse response = mockMvc.perform(delete(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE + "?id={userId}&password={password}", userId, password)
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, accessToken, 1800, true, false)))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.OK.value());
		Optional<User> userAfter = userRepository.findUserByEmailWithRoles("Tester3@gmail.com");
		assertThat(userAfter).isEmpty();
	}

	Long createUser(String email) throws Exception {

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
}