package PizzaApp.api.exceptions;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	// exception handling for pojos validation
	// Order entity when sending POST request from front-end for example

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		ApiErrorDTO errorsDTO = new ApiErrorDTO();

		errorsDTO.setTimeStamp(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH:mm:ss")));
		errorsDTO.setStatusCode(status.value());
		errorsDTO.setPath(request.getDescription(false));

		// Get all errors
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
				.collect(Collectors.toList());

		errorsDTO.setErrors(errors);

		return new ResponseEntity<>(errorsDTO, status);
	}

	// exception handling for path variable validation

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseBody
	protected ResponseEntity<ApiErrorDTO> handleConstraintViolationException(HttpServletRequest request,
			ConstraintViolationException ex) {

		ApiErrorDTO errorsDTO = new ApiErrorDTO();

		errorsDTO.setTimeStamp(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH:mm:ss")));
		errorsDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
		errorsDTO.setPath(request.getServletPath());
		errorsDTO.addError(ex.getMessage());

		return new ResponseEntity<ApiErrorDTO>(errorsDTO, HttpStatus.BAD_REQUEST);
	}

	// exception handling for entity not found in db
	// for findOrderById for example

	@ExceptionHandler(NoResultException.class)
	@ResponseBody
	protected ResponseEntity<ApiErrorDTO> handleNoResult(HttpServletRequest request, NoResultException ex) {

		ApiErrorDTO errorsDTO = new ApiErrorDTO();

		errorsDTO.setTimeStamp(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH:mm:ss")));
		errorsDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
		errorsDTO.setPath(request.getServletPath());
		errorsDTO.addError(ex.getMessage());

		return new ResponseEntity<ApiErrorDTO>(errorsDTO, HttpStatus.NOT_FOUND);
	}
}