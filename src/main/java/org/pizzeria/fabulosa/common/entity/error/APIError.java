package org.pizzeria.fabulosa.common.entity.error;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "error")
@ToString
public class APIError {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "error_generator")
	@SequenceGenerator(name = "error_generator", sequenceName = "error_seq", allocationSize = 1)
	private Long id;

	@NotNull
	private String cause;

	@Column(length = 8000)
	@NotNull
	private String message;

	@NotNull
	private String origin;

	@NotNull
	private String path;

	@NotNull
	private boolean logged;

	@NotNull
	private boolean fatal;

	@NotNull
	private LocalDateTime createdOn;
}