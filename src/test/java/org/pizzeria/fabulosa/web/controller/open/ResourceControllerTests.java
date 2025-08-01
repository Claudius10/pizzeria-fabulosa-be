package org.pizzeria.fabulosa.web.controller.open;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.common.dao.address.AddressRepository;
import org.pizzeria.fabulosa.common.dao.resources.OfferRepository;
import org.pizzeria.fabulosa.common.dao.resources.ProductRepository;
import org.pizzeria.fabulosa.common.dao.resources.StoreRepository;
import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.common.entity.resources.Offer;
import org.pizzeria.fabulosa.common.entity.resources.Product;
import org.pizzeria.fabulosa.common.entity.resources.Store;
import org.pizzeria.fabulosa.web.dto.resource.OfferListDTO;
import org.pizzeria.fabulosa.web.dto.resource.ProductListDTO;
import org.pizzeria.fabulosa.web.dto.resource.StoreListDTO;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
@AutoConfigureMockMvc
class ResourceControllerTests {

	@Container
	@ServiceConnection
	private final static MariaDBContainer db = new MariaDBContainer<>(DockerImageName.parse("mariadb:latest"));

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private StoreRepository storeRepository;

	@BeforeAll
	public void init() {
		Offer offer = new Offer();
		offer.setImage("");
		offer.setName(Map.of("name1", "name1", "name2", "name2"));
		offer.setDescription(Map.of("description1", "description1", "description2", "description2"));
		offer.setCaveat(Map.of("caveat1", "caveat1", "caveat2", "caveat2"));

		//HttpMessageNotReadableException

		productRepository.save(new Product(
				null,
				"pizza",
				"",
				Map.of(),
				Map.of(),
				Map.of("m", Map.of("en", "Medium")),
				Map.of("m", 13.3),
				Map.of("es", List.of(""))));
		offerRepository.save(offer);
		addressRepository.save(Address.builder().withStreet("Street").withNumber(5).build());
		storeRepository.save(new Store(null, "", "", 123, Map.of(),
				Address.builder().withStreet("Street").withNumber(5).build()));
	}

	@Test
	void givenGetProductApiCall_thenReturnResource() throws Exception {
		// Act

		// get api call to find product list
		MockHttpServletResponse response = mockMvc.perform(get(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.RESOURCE_BASE +
								ApiRoutes.RESOURCE_PRODUCT + "?type=pizza&pageNumber=0&pageSize=10"))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		ProductListDTO products = objectMapper.readValue(response.getContentAsString(), ProductListDTO.class);
		assertThat(products.productList()).hasSize(1);
		assertThat(products.productList().getFirst().getFormats().get("m").get("en")).isEqualTo("Medium");
		assertThat(products.productList().getFirst().getPrices().get("m")).isEqualTo(13.3);
	}

	@Test
	void givenGetStoresApiCall_thenReturnResource() throws Exception {
		// Act

		// get api call to find store list
		MockHttpServletResponse response = mockMvc.perform(get(
				ApiRoutes.BASE +
						ApiRoutes.V1 +
						ApiRoutes.RESOURCE_BASE +
						ApiRoutes.RESOURCE_STORE
		)).andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		StoreListDTO storeList = objectMapper.readValue(response.getContentAsString(), StoreListDTO.class);
		assertThat(storeList.stores()).hasSize(1);
	}

	@Test
	void givenGetOffersApiCall_thenReturnResource() throws Exception {
		// Act

		// get api call to find offer list
		MockHttpServletResponse response = mockMvc.perform(get(ApiRoutes.BASE +
				ApiRoutes.V1 +
				ApiRoutes.RESOURCE_BASE +
				ApiRoutes.RESOURCE_OFFER
		)).andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		OfferListDTO offerList = objectMapper.readValue(response.getContentAsString(), OfferListDTO.class);
		assertThat(offerList.offers()).hasSize(1);
	}

	@Test
	void givenGetNow_thenReturnResource() throws Exception {
		// Act

		MockHttpServletResponse response = mockMvc.perform(get(ApiRoutes.BASE +
				ApiRoutes.V1 +
				ApiRoutes.RESOURCE_BASE +
				ApiRoutes.LOCAL_DATE_TIME_NOW
		)).andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		LocalDateTime date = objectMapper.readValue(response.getContentAsString(), LocalDateTime.class);
		assertThat(date).isNotNull();
	}
}