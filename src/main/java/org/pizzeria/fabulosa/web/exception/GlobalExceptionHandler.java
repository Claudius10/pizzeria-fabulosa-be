package org.pizzeria.fabulosa.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.common.dao.error.ErrorRepository;
import org.pizzeria.fabulosa.common.entity.error.Error;
import org.pizzeria.fabulosa.common.util.logger.ExceptionLogger;
import org.pizzeria.fabulosa.security.utils.SecurityCookies;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.property.SecurityProperties;
import org.pizzeria.fabulosa.web.util.ServerUtils;
import org.pizzeria.fabulosa.web.util.constant.ApiResponses;
import org.pizzeria.fabulosa.web.util.constant.SecurityResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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
	protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

		String path = extractPath(request);

		Response response = Response.builder()
				.isError(true)
				.error(Error.builder()
						.id(UUID.randomUUID().getMostSignificantBits())
						.message("See cause")
						.cause(body != null ? body.toString() : "unknown")
						.origin(CLASS_NAME_SHORT)
						.path(path)
						.logged(false)
						.fatal(false)
						.build())
				.build();

		return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request
	) {

		String path = extractPath(request);
		String exSimpleName = ex.getClass().getSimpleName();
		List<String> errorMessages = new ArrayList<>();

		ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
			errorMessages.add(String.format("Field: %s - Error: %s - Value: %s",
					fieldError.getField(),
					fieldError.getDefaultMessage(),
					fieldError.getRejectedValue()));
		});

		Response response = Response.builder()
				.isError(true)
				.error(Error.builder()
						.id(UUID.randomUUID().getMostSignificantBits())
						.cause(exSimpleName)
						.message(String.valueOf(errorMessages))
						.origin(CLASS_NAME_SHORT + exSimpleName)
						.path(path)
						.logged(false)
						.fatal(false)
						.build())
				.build();

		ExceptionLogger.log(ex, log, response);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
	protected ResponseEntity<Response> securityException(RuntimeException ex, WebRequest request) {
		String path = extractPath(request);

		// first handle all the bots trying to access random endpoints
		if (ex instanceof InsufficientAuthenticationException && "/error".equals(path)) {
			log.warn("AuthenticationException redirected to /error, URL {}", extractURL(request));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		Response response;

		switch (ex) {
			case AuthenticationException authenticationException -> response = handleAuthenticationException(authenticationException, request, path);
			case AccessDeniedException accessDeniedException -> response = handleAccessDenied(accessDeniedException, path);
			default -> response = handleUnknownError(ex, ex.getClass().getSimpleName(), path);
		}

		ExceptionLogger.log(ex, log, response);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Response> unknownException(Exception ex, WebRequest request) {
		String path = extractPath(request);
		Response response = handleUnknownError(ex, "unknownException", path);
		ExceptionLogger.log(ex, log, response);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	private Response handleAccessDenied(AccessDeniedException ex, String path) {

		String exSimpleName = ex.getClass().getSimpleName();

		Error error = Error.builder()
				.id(UUID.randomUUID().getMostSignificantBits())
				.cause(exSimpleName)
				.message(ex.getMessage())
				.origin(CLASS_NAME_SHORT + exSimpleName)
				.path(path)
				.logged(false)
				.fatal(true)
				.build();

		return Response.builder().isError(true).error(error).build();
	}

	private Response handleAuthenticationException(AuthenticationException ex, WebRequest request, String path) {
		String exSimpleName = ex.getClass().getSimpleName();
		String errorMessage;
		boolean fatal = false;
		boolean logged = false;
		boolean deleteCookies = false;

		switch (ex) {
			case BadCredentialsException ignored -> errorMessage = SecurityResponses.BAD_CREDENTIALS;
			case UsernameNotFoundException ignored -> errorMessage = ApiResponses.USER_NOT_FOUND;
			case InvalidBearerTokenException ignored -> {
				errorMessage = SecurityResponses.INVALID_TOKEN;
				logged = true;
				deleteCookies = true;
			}
			case AuthenticationCredentialsNotFoundException ignored -> {
				errorMessage = ex.getMessage();
				logged = true;
			}
			case InternalAuthenticationServiceException ignored -> {
				errorMessage = ex.getMessage();
				logged = true;
			}
			default -> {
				fatal = true;
				logged = true;
				errorMessage = ex.getMessage();
			}
		}

		Error error = Error.builder()
				.id(UUID.randomUUID().getMostSignificantBits())
				.cause(exSimpleName)
				.message(errorMessage)
				.origin(CLASS_NAME_SHORT + exSimpleName)
				.path(path)
				.logged(logged)
				.fatal(fatal)
				.build();

		if (logged) {
			error.setId(null);
			error.setCreatedOn(LocalDateTime.now());
			Error savedError = this.errorRepository.save(error);
			error.setId(savedError.getId());
		}

		if (deleteCookies) {
			SecurityCookies.eatAllCookies(((ServletWebRequest) request).getRequest(),
					((ServletWebRequest) request).getResponse(), securityProperties.getCookies().getDomain());
		}

		return Response.builder().isError(true).error(error).build();
	}

	private Response handleUnknownError(Exception ex, String details, String path) {

		Error error = Error.builder()
				.cause(ex.getClass().getSimpleName())
				.message(ex.getMessage())
				.origin(CLASS_NAME_SHORT + details)
				.path(path)
				.logged(true)
				.fatal(true)
				.build();

		error.setCreatedOn(LocalDateTime.now());
		Error savedError = errorRepository.save(error);
		error.setId(savedError.getId());

		return Response.builder().isError(true).error(error).build();
	}

	private String extractPath(WebRequest request) {
		HttpServletRequest httpRequest = ((ServletWebRequest) request).getRequest();
		return ServerUtils.resolvePath(httpRequest.getServletPath(), httpRequest.getRequestURI());
	}

	private String extractURL(WebRequest request) {
		HttpServletRequest httpRequest = ((ServletWebRequest) request).getRequest();
		return httpRequest.getRequestURL().toString();
	}
}