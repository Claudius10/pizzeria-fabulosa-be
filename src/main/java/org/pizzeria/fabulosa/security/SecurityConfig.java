package org.pizzeria.fabulosa.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.security.access.AccessDeniedHandler;
import org.pizzeria.fabulosa.security.access.AuthenticationHandler;
import org.pizzeria.fabulosa.security.access.ClearCookiesLogoutHandler;
import org.pizzeria.fabulosa.security.access.CookieBearerTokenResolver;
import org.pizzeria.fabulosa.security.access.login.InvalidLoginHandler;
import org.pizzeria.fabulosa.security.access.login.ValidLoginHandler;
import org.pizzeria.fabulosa.security.filters.UnknownPathFilter;
import org.pizzeria.fabulosa.security.ssl.JWTKeys;
import org.pizzeria.fabulosa.web.property.SecurityProperties;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final SecurityProperties securityProperties;

	@Bean
	SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			JwtDecoder jwtDecoder,
			ValidLoginHandler validAuthHandler,
			InvalidLoginHandler invalidAuthHandler,
			ClearCookiesLogoutHandler clearCookiesLogoutHandler,
			AuthenticationHandler authenticationHandler,
			AccessDeniedHandler accessDeniedHandler,
			CookieBearerTokenResolver cookieBearerTokenResolver
	) throws Exception {
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		http.csrf(AbstractHttpConfigurer::disable);

	/*	http.csrf(csrf -> {

			csrf.csrfTokenRepository(csrfTokenRepository()); // persist CSRF token in a cookie
			csrf.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()); // get CSRF token off cookie

			// https://github.com/spring-projects/spring-security/issues/8668 for
			// enabling CSRF token protection for post auth requests with JWT tokens
			csrf.withObjectPostProcessor(new ObjectPostProcessor<CsrfFilter>() {
				@Override
				public <O extends CsrfFilter> O postProcess(O object) {
					object.setRequireCsrfProtectionMatcher(csrfProtectionMatcher());
					return object;
				}
			});
		});*/

		// throw out requests to unmapped paths (first filter in the chain)
		http.addFilterBefore(new UnknownPathFilter(), ChannelProcessingFilter.class);

		http.authorizeHttpRequests(authorize -> {
			authorize.requestMatchers(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.RESOURCE_BASE + ApiRoutes.ALL).permitAll();
			authorize.requestMatchers(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE + ApiRoutes.ALL).permitAll();
			authorize.requestMatchers(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.DOCS + ApiRoutes.ALL).permitAll();
			authorize.requestMatchers(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.DOCS + ".yaml").permitAll();
			authorize.requestMatchers(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.ALL).hasAnyRole("USER");
			authorize.requestMatchers("/api/tests/admin").hasRole("ADMIN");
			authorize.requestMatchers("/api/tests/**").hasRole("USER");
			authorize.anyRequest().denyAll();
		});

		http.formLogin(formLogin -> {
			formLogin.permitAll();
			formLogin.successHandler(validAuthHandler);
			formLogin.failureHandler(invalidAuthHandler);
		});

		http.logout(logout -> {
			logout.permitAll();
			logout.addLogoutHandler(clearCookiesLogoutHandler);
			logout.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()); // return 200 on successful logout
		});

		// post authentication JWT resource protection
		http.oauth2ResourceServer(oauth2ResourceServer -> {
			oauth2ResourceServer.jwt(jwt -> {
				jwt.decoder(jwtDecoder);
				jwt.jwtAuthenticationConverter(jwtAuthenticationConverter());
			});
			oauth2ResourceServer.authenticationEntryPoint(authenticationHandler); // handle jwt auth failure
			oauth2ResourceServer.accessDeniedHandler(accessDeniedHandler); // handle jwt access denied
			oauth2ResourceServer.bearerTokenResolver(cookieBearerTokenResolver); // load JWT from cookie
		});

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(6);
	}

	@Bean
	AuthenticationManager authManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);
		authenticationProvider.setHideUserNotFoundExceptions(false);
		return new ProviderManager(authenticationProvider);
	}

	@Bean
	JwtEncoder jwtEncoder(JWTKeys keys) {
		JWK jwk = new RSAKey.Builder(keys.getPublicKey()).privateKey(keys.getPrivateKey()).build();
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}

	@Bean
	JwtDecoder jwtDecoder(JWTKeys keys) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
		decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(securityProperties.getTokenIssuer()));
		return decoder;
	}

	/*
	Set Access-Control-Allow-Headers when allowing headers to be passed from the client to the server (e.g. If-Match).
	Set Access-Control-Expose-Headers when allowing headers to be passed back from the server to the client (e.g. ETag).
	*/
	private CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(securityProperties.getAllowedOrigins());
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "X-Requested-With", "Accept"));
		configuration.setExposedHeaders(Arrays.asList("Content-Type", "Authorization", "Set-Cookie"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

/*	private RequestMatcher csrfProtectionMatcher() {
		// custom CsrfFilter protection matcher implementation
		AntPathRequestMatcher[] noCsrfTokenRoutes = {
				new AntPathRequestMatcher("/api/resource/**"),
				new AntPathRequestMatcher("/api/anon/**")
		};

		return request -> {
			for (AntPathRequestMatcher rm : noCsrfTokenRoutes) {
				if (rm.matches(request)) {
					return false;
				}
			}

			// default CsrfFilter protection matcher behavior
			Set<String> allowedMethods = new HashSet<>(Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS"));
			return !allowedMethods.contains(request.getMethod());
		};
	}

	private CookieCsrfTokenRepository csrfTokenRepository() {
		CookieCsrfTokenRepository result = new CookieCsrfTokenRepository();
		result.setCookieCustomizer((cookie) -> {
			cookie.httpOnly(true);
			//cookie.secure(true); // NOTE - on for prod fe
			//cookie.domain("up.railway.app"); // NOTE - on for prod fe
		});
		return result;
	}*/

	// change the auto added prefix to roles from SCOPE_ to ROLE_
	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
		JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
		jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		return jwtConverter;
	}
}