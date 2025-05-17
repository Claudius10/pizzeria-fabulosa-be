package org.pizzeria.fabulosa.web.dto.resource;

import org.pizzeria.fabulosa.common.entity.resources.Offer;

import java.util.List;

public record OfferListDTO(
		List<Offer> offers
) {
}
