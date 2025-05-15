package org.pizzeria.fabulosa.common.entity.resources;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.Map;

@Entity
@Table(name = "offer")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Offer {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "offer_generator")
	@SequenceGenerator(name = "offer_generator", sequenceName = "offer_seq", allocationSize = 1)
	private Long id;

	private String image;

	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, String> name; // <"es":"texto", "en":"text">

	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, String> description;

	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, String> caveat;
}