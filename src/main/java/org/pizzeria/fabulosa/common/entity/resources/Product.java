package org.pizzeria.fabulosa.common.entity.resources;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_generator")
	@SequenceGenerator(name = "product_generator", sequenceName = "product_seq", allocationSize = 1)
	private Long id;

	@NotNull
	private String type;

	@NotNull
	private String image;

	@NotNull
	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, String> name;

	@NotNull
	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, List<String>> description;

	@NotNull
	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, Map<String, String>> formats; // <"m", <"en": "Medium">, <"es": "Mediana">; "l", <"en": "Familiar">,
	// <"es": "Familiar">>

	@NotNull
	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, Double> prices;

	@NotNull
	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, List<String>> allergens;
}