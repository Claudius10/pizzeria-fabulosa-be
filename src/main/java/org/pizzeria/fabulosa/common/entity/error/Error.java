package org.pizzeria.fabulosa.common.entity.error;

import jakarta.persistence.*;
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
public class Error {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "error_generator")
	@SequenceGenerator(name = "error_generator", sequenceName = "error_seq", allocationSize = 1)
	private Long id;

	private String cause;

	@Column(length = 8000)
	private String message;

	private String origin;

	private String path;

	private boolean logged;

	private boolean fatal;

	private LocalDateTime createdOn;
}