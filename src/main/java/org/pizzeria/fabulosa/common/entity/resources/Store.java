package org.pizzeria.fabulosa.common.entity.resources;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.pizzeria.fabulosa.common.entity.address.Address;

import java.util.Map;

@Entity(name = "Store")
@Table(name = "store")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Store {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "store_generator")
	@SequenceGenerator(name = "store_generator", sequenceName = "store_seq", allocationSize = 1)
	private Long id;

	private String image;

	private String name;

	private Integer phoneNumber;

	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, String> schedule;

	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	private Address address;
}