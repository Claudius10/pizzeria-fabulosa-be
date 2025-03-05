package org.pizzeria.fabulosa.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.Test;
import org.pizzeria.fabulosa.configs.properties.SSLProperties;
import org.pizzeria.fabulosa.configs.properties.SecurityProperties;
import org.pizzeria.fabulosa.configs.web.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.configs.web.security.ssl.JWTKeys;
import org.pizzeria.fabulosa.entity.role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class JWTTests {

	@Test
	public void signatureTest() {
		// Arrange

		List<GrantedAuthority> roles = new ArrayList<>();
		Role role = new Role("USER");
		roles.add(role);

		SSLProperties sslProperties = new SSLProperties();
		sslProperties.setKeyStoreType("PKCS12");
		JWTKeys jwtKeys = new JWTKeys(sslProperties);

		jwtKeys.init();
		JwtDecoder jwtDecoder = jwtDecoder(jwtKeys);
		SecurityProperties securityProperties = new SecurityProperties();
		securityProperties.setTokenIssuer("test");
		JWTTokenManager tokenManager = new JWTTokenManager(jwtEncoder(jwtKeys), securityProperties);

		// Act

		String test = tokenManager.getAccessToken("test", roles, 1L);
		Jwt decode = jwtDecoder.decode(test);

		// Assert

		Map<String, Object> claims = decode.getClaims();
		assertThat(claims.get("sub")).isEqualTo("test");
	}

	JwtEncoder jwtEncoder(JWTKeys keys) {
		JWK jwk = new RSAKey.Builder(keys.getPublicKey()).privateKey(keys.getPrivateKey()).build();
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}

	JwtDecoder jwtDecoder(JWTKeys keys) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
		decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("test"));
		return decoder;
	}

	@Test
	void givenInValidStoreType_initBackup() {

		// Arrange

		SSLProperties sslProperties = new SSLProperties();
		sslProperties.setKeyStoreType("error");
		JWTKeys jwtKeys = new JWTKeys(sslProperties);

		// Act

		jwtKeys.init();

		// Assert

		assertThat(jwtKeys.getPublicKey()).isNotNull();
		assertThat(jwtKeys.getPrivateKey()).isNotNull();
	}

	@Test
	void givenException_initBackup() {

		// Arrange

		SSLProperties sslProperties = new SSLProperties();
		sslProperties.setKeyStoreType("PKCS12");
		JWTKeys jwtKeys = new JWTKeys(sslProperties);

		// Act

		jwtKeys.init();

		// Assert

		assertThat(jwtKeys.getPublicKey()).isNotNull();
		assertThat(jwtKeys.getPrivateKey()).isNotNull();
	}
}
