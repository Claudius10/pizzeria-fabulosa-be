package org.pizzeria.fabulosa.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class TestUtils {

	public static Response getResponse(MockHttpServletResponse response, ObjectMapper mapper) throws JsonProcessingException, UnsupportedEncodingException {
		return mapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), Response.class);
	}
}