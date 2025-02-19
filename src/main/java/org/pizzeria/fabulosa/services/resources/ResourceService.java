package org.pizzeria.fabulosa.services.resources;

import org.pizzeria.fabulosa.entity.resources.Offer;
import org.pizzeria.fabulosa.entity.resources.Product;
import org.pizzeria.fabulosa.entity.resources.Store;

import java.util.List;

public interface ResourceService {

	List<Product> findAllProductsByType(String type);

	List<Offer> findAllOffers();

	List<Store> findAllStores();
}