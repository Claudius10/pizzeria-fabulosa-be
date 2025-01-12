package org.pizzeria.fabulosa.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.Test;
import org.pizzeria.fabulosa.configs.web.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.configs.web.security.keys.RSAKeyPair;
import org.pizzeria.fabulosa.entity.role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.utils.Constants.ISSUER;

public class JWTTokenManagerTests {

	@Test
	public void signatureTest() {
		// Arrange

		List<GrantedAuthority> roles = new ArrayList<>();
		Role role = new Role("USER");
		roles.add(role);

		RSAKeyPair rsaKeyPair = new RSAKeyPair();
		JwtDecoder jwtDecoder = jwtDecoder(rsaKeyPair);
		JWTTokenManager tokenManager = new JWTTokenManager(jwtEncoder(rsaKeyPair));

		// Act

		String test = tokenManager.getAccessToken("test", roles, 1L);
		Jwt decode = jwtDecoder.decode(test);

		// Assert

		Map<String, Object> claims = decode.getClaims();
		assertThat(claims.get("sub")).isEqualTo("test");
	}

	JwtEncoder jwtEncoder(RSAKeyPair keys) {
		JWK jwk = new RSAKey.Builder(keys.getPublicKey()).privateKey(keys.getPrivateKey()).build();
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}

	JwtDecoder jwtDecoder(RSAKeyPair keys) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
		decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(ISSUER));
		return decoder;
	}
}
