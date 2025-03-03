package org.pizzeria.fabulosa.web.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.configs.properties.SecurityProperties;
import org.pizzeria.fabulosa.configs.web.security.utils.SecurityCookieUtils;
import org.pizzeria.fabulosa.entity.error.Error;
import org.pizzeria.fabulosa.repos.error.ErrorRepository;
import org.pizzeria.fabulosa.utils.ExceptionLogger;
import org.pizzeria.fabulosa.web.constants.ApiResponses;
import org.pizzeria.fabulosa.web.constants.SecurityResponses;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.api.Status;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String CLASS_NAME_SHORT = "G.E.H";

	private final ErrorRepository errorRepository;

	private final SecurityProperties securityProperties;

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request
	) {

		List<String> errorMessages = new ArrayList<>();

		ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
			errorMessages.add(String.format("Field: %s - Error: %s - Value: %s",
					fieldError.getField(),
					fieldError.getDefaultMessage(),
					fieldError.getRejectedValue()));
		});

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.BAD_REQUEST.name())
						.code(HttpStatus.BAD_REQUEST.value())
						.isError(true)
						.build())
				.error(Error.builder()
						.id(UUID.randomUUID().getMostSignificantBits())
						.cause(ex.getClass().getSimpleName())
						.message(String.valueOf(errorMessages))
						.origin(CLASS_NAME_SHORT + ".handleMethodArgumentNotValid")
						.path(((ServletWebRequest) request).getRequest().getServletPath())
						.logged(false)
						.fatal(false)
						.build())
				.build();

		ExceptionLogger.log(ex, log, response);
		return ResponseEntity.status(HttpStatus.OK).body(response); // return OK to get the ResponseDTO in onSuccess callback
	}

	@ExceptionHandler({DataAccessException.class})
	protected ResponseEntity<Response> dataAccessException(DataAccessException ex, WebRequest request) {

		boolean fatal = true;

		if (ex instanceof DataIntegrityViolationException && ex.getMessage() != null && ex.getMessage().contains("constraint [USER_EMAIL]")) {
			fatal = false;
		} else if (ex instanceof DataRetrievalFailureException && ApiResponses.ORDER_NOT_FOUND.equals(ex.getMessage())) {
			fatal = false;
		}

		Error error = Error.builder()
				.id(UUID.randomUUID().getMostSignificantBits())
				.cause(!fatal ? ApiResponses.USER_EMAIL_ALREADY_EXISTS : ex.getClass().getSimpleName())
				.origin(CLASS_NAME_SHORT + ".dataAccessException")
				.path(((ServletWebRequest) request).getRequest().getPathInfo())
				.message(!fatal ? null : ex.getMessage())
				.logged(fatal)
				.fatal(fatal)
				.build();

		if (fatal) {
			error.setId(null);
			error.setCreatedOn(LocalDateTime.now());
			Error savedError = errorRepository.save(error);
			error.setId(savedError.getId());
		}

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.BAD_REQUEST.name())
						.code(HttpStatus.BAD_REQUEST.value())
						.isError(true)
						.build())
				.error(error)
				.build();

		ExceptionLogger.log(ex, log, response);
		return ResponseEntity.status(HttpStatus.OK).body(response); // return OK to get the ResponseDTO in onSuccess callback
	}

	@ExceptionHandler(AccessDeniedException.class)
	protected ResponseEntity<Response> accessDeniedException(AccessDeniedException ex, WebRequest request) {

		Error error = Error.builder()
				.id(UUID.randomUUID().getMostSignificantBits())
				.cause(ex.getClass().getSimpleName())
				.message(ex.getMessage())
				.origin(CLASS_NAME_SHORT + ".accessDeniedException")
				.path(((ServletWebRequest) request).getRequest().getServletPath())
				.logged(false)
				.fatal(true)
				.build();

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.UNAUTHORIZED.name())
						.code(HttpStatus.UNAUTHORIZED.value())
						.isError(true)
						.build())
				.error(error)
				.build();

		ExceptionLogger.log(ex, log, response);
		return ResponseEntity.status(HttpStatus.OK).body(response); // return OK to get the ResponseDTO in onSuccess callback
	}

	@ExceptionHandler(AuthenticationException.class)
	protected ResponseEntity<Response> authenticationException(AuthenticationException ex, WebRequest request) {

		String errorMessage;
		boolean fatal = false;
		boolean deleteCookies = false;

		switch (ex) {
			case BadCredentialsException ignored -> errorMessage = SecurityResponses.BAD_CREDENTIALS;
			case UsernameNotFoundException ignored -> errorMessage = SecurityResponses.USER_NOT_FOUND;
			case InvalidBearerTokenException ignored -> {
				errorMessage = SecurityResponses.INVALID_TOKEN;
				deleteCookies = true;
			}
			default -> {
				fatal = true;
				errorMessage = ex.getMessage();
			}
		}

		Error error = Error.builder()
				.id(UUID.randomUUID().getMostSignificantBits())
				.cause(ex.getClass().getSimpleName())
				.message(errorMessage)
				.origin(CLASS_NAME_SHORT + ".authenticationException")
				.path(((ServletWebRequest) request).getRequest().getPathInfo())
				.logged(fatal)
				.fatal(fatal)
				.build();

		if (fatal) {
			error.setId(null);
			error.setCreatedOn(LocalDateTime.now());
			Error savedError = this.errorRepository.save(error);
			error.setId(savedError.getId());
		}

		if (deleteCookies) {
			SecurityCookieUtils.eatAllCookies(((ServletWebRequest) request).getRequest(),
					((ServletWebRequest) request).getResponse(), securityProperties.getCookies().getDomain());
		}

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.UNAUTHORIZED.name())
						.code(HttpStatus.UNAUTHORIZED.value())
						.isError(true)
						.build())
				.error(error)
				.build();

		ExceptionLogger.log(ex, log, response);
		return ResponseEntity.status(HttpStatus.OK).body(response); // return OK to get the ResponseDTO in onSuccess callback
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Response> unknownException(Exception ex, WebRequest request) {

		Error error = Error.builder()
				.cause(ex.getClass().getSimpleName())
				.message(ex.getMessage())
				.origin(CLASS_NAME_SHORT + ".unknownException")
				.path(((ServletWebRequest) request).getRequest().getServletPath())
				.logged(true)
				.fatal(true)
				.build();

		errorRepository.save(error);

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.INTERNAL_SERVER_ERROR.name())
						.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
						.isError(true)
						.build())
				.error(error)
				.build();

		ExceptionLogger.log(ex, log, response);
		return ResponseEntity.status(HttpStatus.OK).body(response); // return OK to get the ResponseDTO in onSuccess callback
	}
}