package PizzaApp.api.utility.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class JWTUtils {

	private final JwtEncoder jwtEncoder;
	private final JwtDecoder jwtDecoder;

	public JWTUtils(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
		this.jwtEncoder = jwtEncoder;
		this.jwtDecoder = jwtDecoder;
	}

	public String createAccessToken(String name, Collection<? extends GrantedAuthority> roles) {
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("http://192.168.1.11:8090")
				.subject(name)
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plus(20, ChronoUnit.SECONDS))
				.claim("roles", parseRoles(roles))
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	public String createRefreshToken(String name, Collection<? extends GrantedAuthority> roles) {
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("http://192.168.1.11:8090")
				.subject(name)
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plus(60, ChronoUnit.SECONDS))
				.claim("roles", parseRoles(roles))
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	public Jwt validate(String refreshToken) {
		return jwtDecoder.decode(refreshToken);
	}

	public String parseRoles(Collection<? extends GrantedAuthority> authorities) {
		return authorities.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));
	}
}
