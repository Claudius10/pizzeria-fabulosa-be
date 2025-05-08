package org.pizzeria.fabulosa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.repos.address.AddressRepository;
import org.pizzeria.fabulosa.services.address.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
class AddressServiceTests {

	@Container
	@ServiceConnection
	private final static MariaDBContainer db = new MariaDBContainer<>(DockerImageName.parse("mariadb:latest"));

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private AddressService addressService;

	@Test
	void givenAddress_thenCreateAddress() {
		// Arrange

		Address testAddress = Address.builder()
				.withStreet("Test Street")
				.withNumber(9)
				.build();

		// Act

		addressService.create(testAddress);
		long addressCount = addressRepository.count();

		// Assert
		assertThat(addressCount).isEqualTo(1);
	}

	@Test
	void givenAddress_whenExample_thenFindAddress() {
		// Arrange

		Address testAddress = Address.builder()
				.withStreet("Test Street")
				.withNumber(9)
				.build();

		// Act

		addressService.create(testAddress);
		Optional<Address> address = addressService.findByExample(testAddress);

		// Assert
		assertThat(address.get().getStreet()).isEqualTo(testAddress.getStreet());
	}

	@Test
	void givenAddressId_thenFindAddressById() {
		// Arrange

		Address testAddress = Address.builder()
				.withStreet("Test Street")
				.withNumber(9)
				.build();

		// Act

		Long addressId = addressService.create(testAddress);
		Optional<Address> address = addressService.findAddressById(addressId);

		// Assert
		assertThat(address.get().getId()).isEqualTo(addressId);
	}
}