package org.pizzeria.fabulosa.common.entity.address;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.pizzeria.fabulosa.web.dto.order.AddressDTO;

@Entity(name = "Address")
@Table(name = "address")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@EqualsAndHashCode
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_generator")
	@SequenceGenerator(name = "address_generator", sequenceName = "address_seq", allocationSize = 1)
	private Long id;

	@Column(nullable = false)
	@NotNull
	private String street;

	@Column(nullable = false)
	@NotNull
	private Integer number;

	private String details;

	public static FromDTO fromDTOBuilder() {
		return new FromDTO();
	}

	public static class FromDTO {
		private FromDTO() {
		}

		public Address build(AddressDTO dto) {
			return Address.builder()
					.withStreet(dto.street())
					.withNumber(dto.number())
					.withDetails(dto.details())
					.build();
		}
	}
}