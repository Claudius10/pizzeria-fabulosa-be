package org.pizzeria.fabulosa.web.dto.resource;

import org.pizzeria.fabulosa.common.entity.resources.Store;

import java.util.List;

public record StoreListDTO(
		List<Store> stores
) {
}
