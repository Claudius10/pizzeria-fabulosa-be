package org.pizzeria.fabulosa.web.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;
import org.pizzeria.fabulosa.web.validation.constraints.annotation.DoubleLength;
import org.pizzeria.fabulosa.web.validation.constraints.annotation.IntegerLength;

import java.util.List;
import java.util.Map;

public record CartItemDTO(
		
		Long id,

		@NotBlank
		String type,

		@NotNull
		@DoubleLength(min = 1, max = 5)
		Double price,

		@NotNull
		@IntegerLength(min = 1, max = 2, message = ValidationResponses.CART_ITEM_MAX_QUANTITY_ERROR)
		Integer quantity,

		@NotNull
		Map<String, String> name,

		@NotNull
		Map<String, List<String>> description,

		@NotNull
		Map<String, Map<String, String>> formats
) {
}
