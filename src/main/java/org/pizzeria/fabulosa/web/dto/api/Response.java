package org.pizzeria.fabulosa.web.dto.api;

import lombok.*;
import org.pizzeria.fabulosa.common.entity.error.Error;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Response {

	@Builder.Default
	private final String timeStamp = LocalDateTime.now().toString();

	@Builder.Default
	private Boolean isError = false;

	private Object payload;

	private Error error;
}