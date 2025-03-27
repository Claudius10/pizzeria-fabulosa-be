package org.pizzeria.fabulosa.services.resources;

import org.pizzeria.fabulosa.entity.resources.Offer;
import org.pizzeria.fabulosa.entity.resources.Product;
import org.pizzeria.fabulosa.entity.resources.Store;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ResourceService {

	Page<Product> findAllProductsByType(String type, int size, int page);

	List<Offer> findAllOffers();

	List<Store> findAllStores();
}