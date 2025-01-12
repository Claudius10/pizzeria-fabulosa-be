package org.pizzeria.fabulosa.repos.resources;

import org.pizzeria.fabulosa.entity.resources.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

}