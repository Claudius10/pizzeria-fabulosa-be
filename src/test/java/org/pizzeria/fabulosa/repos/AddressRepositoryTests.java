package org.pizzeria.fabulosa.repos;

import org.junit.jupiter.api.Test;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.repos.address.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
class AddressRepositoryTests {

	@Autowired
	private AddressRepository addressRepository;

	@Test
	void givenAddress_thenCreateAddress() {
		// Arrange

		Address testAddress = Address.builder()
				.withStreet("Test Street")
				.withNumber(9)
				.build();

		// Act

		addressRepository.save(testAddress);
		long addressCount = addressRepository.count();

		// Assert
		assertThat(addressCount).isEqualTo(1);
	}
}