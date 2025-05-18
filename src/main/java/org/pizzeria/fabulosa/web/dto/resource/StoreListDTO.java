package org.pizzeria.fabulosa.web.dto.resource;

import jakarta.validation.constraints.NotNull;
import org.pizzeria.fabulosa.common.entity.resources.Store;

import java.util.List;

public record StoreListDTO(
		@NotNull
		List<Store> stores
) {
}
