package org.pizzeria.fabulosa.services.resources;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.pizzeria.fabulosa.entity.resources.Offer;
import org.pizzeria.fabulosa.entity.resources.Product;
import org.pizzeria.fabulosa.entity.resources.Store;
import org.pizzeria.fabulosa.repos.resources.OfferRepository;
import org.pizzeria.fabulosa.repos.resources.ProductRepository;
import org.pizzeria.fabulosa.repos.resources.StoreRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ResourceServiceImpl implements ResourceService {

	private final ProductRepository productRepository;

	private final StoreRepository storeRepository;

	private final OfferRepository offerRepository;

	@Cacheable("allProducts")
	@Override
	public List<Product> findAllProducts() {
		return productRepository.findAll();
	}

	@Cacheable("productsByType")
	@Override
	public List<Product> findAllProductsByType(String productType) {
		return productRepository.findAllByProductType(productType);
	}

	@Cacheable("offers")
	@Override
	public List<Offer> findAllOffers() {
		return offerRepository.findAll();
	}

	@Cacheable("stores")
	@Override
	public List<Store> findAllStores() {
		return storeRepository.findAll();
	}
}