package org.pizzeria.fabulosa.services.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.repos.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.pizzeria.fabulosa.web.constants.SecurityResponses.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthenticationService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findUserByEmailWithRoles(username)
				.orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND)); // this ends up as AuthenticationException
	}
}