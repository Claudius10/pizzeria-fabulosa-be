package org.pizzeria.fabulosa.repos.resources;

import org.pizzeria.fabulosa.entity.resources.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findAllByType(String type, Pageable pageable);
}