package org.pizzeria.fabulosa.web.dto.api;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.pizzeria.fabulosa.common.entity.error.APIError;

@Builder
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Response {

	@NotNull
	private final APIError apiError;
}