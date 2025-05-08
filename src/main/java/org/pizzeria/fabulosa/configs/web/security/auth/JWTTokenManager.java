package org.pizzeria.fabulosa.configs.web.security.auth;

import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.configs.properties.SecurityProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JWTTokenManager {

	private final JwtEncoder jwtEncoder;

	private final SecurityProperties securityProperties;

	public String generateAccessToken(String subject, Collection<? extends GrantedAuthority> roles, Long userId) {
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuedAt(Instant.now())
				.issuer(securityProperties.getTokenIssuer())
				.expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
				.subject(subject)
				.claim("roles", parseAuthorities(roles))
				.claim("userId", String.valueOf(userId))
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	public String generateIdToken(String subject, String userName, Long userId, Integer contactNumber) {
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuedAt(Instant.now())
				.issuer(securityProperties.getTokenIssuer())
				.expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
				.subject(subject)
				.claim("name", userName)
				.claim("id", String.valueOf(userId))
				.claim("contactNumber", String.valueOf(contactNumber))
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	private String parseAuthorities(Collection<? extends GrantedAuthority> authorities) {
		return authorities.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));
	}
}