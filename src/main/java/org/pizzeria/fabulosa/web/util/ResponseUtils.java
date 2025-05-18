package org.pizzeria.fabulosa.web.util;

import org.pizzeria.fabulosa.common.entity.error.APIError;
import org.pizzeria.fabulosa.common.util.TimeUtils;
import org.pizzeria.fabulosa.web.dto.api.Response;

import java.util.UUID;

public final class ResponseUtils {

	public static Response error(String origin, String reason, String path) {
		return Response.builder()
				.apiError(APIError.builder()
						.id(UUID.randomUUID().getMostSignificantBits())
						.createdOn(TimeUtils.getNowAccountingDST())
						.cause(reason)
						.origin(origin)
						.path(path)
						.logged(false)
						.fatal(false)
						.build())
				.build();
	}
}
