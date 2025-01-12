package org.pizzeria.fabulosa.web.dto.api;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Status {

	private int code;

	private String description;

	private boolean isError;
}
