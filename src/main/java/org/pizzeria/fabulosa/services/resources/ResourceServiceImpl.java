package org.pizzeria.fabulosa.services.resources;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.entity.resources.Offer;
import org.pizzeria.fabulosa.entity.resources.Product;
import org.pizzeria.fabulosa.entity.resources.Store;
import org.pizzeria.fabulosa.repos.resources.OfferRepository;
import org.pizzeria.fabulosa.repos.resources.ProductRepository;
import org.pizzeria.fabulosa.repos.resources.StoreRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceServiceImpl implements ResourceService {

	private final ProductRepository productRepository;

	private final StoreRepository storeRepository;

	private final OfferRepository offerRepository;

	@Cacheable("productsByType")
	@Override
	public Page<Product> findAllProductsByType(String productType, int size, int page) {

		Sort.TypedSort<Product> product = Sort.sort(Product.class);
		Sort sort = product.by(Product::getId).descending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);

		return productRepository.findAllByType(productType, pageRequest);
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