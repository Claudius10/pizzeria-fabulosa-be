package org.pizzeria.fabulosa.entity.resources;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

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

	private String code;

	private String productType;

	private String image;

	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, String> name;

	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, String> description;

	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, Double> prices;

	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, Map<String, String>> formats; // <"m", <"en": "Medium">, <"es": "Mediana">; "l", <"en": "Familiar">,
	// <"es": "Familiar">>
}