package org.pizzeria.fabulosa.web.dto.resource;

import jakarta.validation.constraints.NotNull;
import org.pizzeria.fabulosa.common.entity.resources.Offer;

import java.util.List;

public record OfferListDTO(
		@NotNull
		List<Offer> offers
) {
}
