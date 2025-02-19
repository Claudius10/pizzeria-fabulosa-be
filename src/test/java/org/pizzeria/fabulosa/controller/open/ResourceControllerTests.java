package org.pizzeria.fabulosa.controller.open;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.resources.Offer;
import org.pizzeria.fabulosa.entity.resources.Product;
import org.pizzeria.fabulosa.entity.resources.Store;
import org.pizzeria.fabulosa.repos.address.AddressRepository;
import org.pizzeria.fabulosa.repos.resources.OfferRepository;
import org.pizzeria.fabulosa.repos.resources.ProductRepository;
import org.pizzeria.fabulosa.repos.resources.StoreRepository;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.utils.TestUtils.getResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@DirtiesContext
class ResourceControllerTests {

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
								ApiRoutes.RESOURCE_PRODUCT + "?type=pizza"))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		List<Product> productList = objectMapper.convertValue(responseObj.getPayload(), List.class);
		assertThat(productList).hasSize(1);
		Product product = objectMapper.convertValue(productList.get(0), Product.class);
		assertThat(product.getFormats().get("m").get("en")).isEqualTo("Medium");
		assertThat(product.getPrices().get("m")).isEqualTo(13.3);
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

		Response responseObj = getResponse(response, objectMapper);
		List<Store> storeList = objectMapper.convertValue(responseObj.getPayload(), List.class);
		assertThat(storeList).hasSize(1);
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

		Response responseObj = getResponse(response, objectMapper);
		List<Offer> offerList = objectMapper.convertValue(responseObj.getPayload(), List.class);
		assertThat(offerList).hasSize(1);
	}

//	@Test
//	void givenGetAllProducts_thenReturnResource() throws Exception {
//		// Act
//
//		// get api call to find offer list
//		MockHttpServletResponse response = mockMvc.perform(get(ApiRoutes.BASE +
//				ApiRoutes.V1 +
//				ApiRoutes.RESOURCE_BASE +
//				ApiRoutes.RESOURCE_PRODUCT
//		)).andReturn().getResponse();
//
//		// Assert
//
//		Response responseObj = getResponse(response, objectMapper);
//		List<Product> productList = objectMapper.convertValue(responseObj.getPayload(), List.class);
//		assertThat(productList).hasSize(1);
//	}
}