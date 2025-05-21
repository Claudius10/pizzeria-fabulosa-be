package org.pizzeria.fabulosa.common.service.user.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.common.dao.internal.UserRepositoryInternal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.pizzeria.fabulosa.web.util.constant.ApiResponses.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthenticationServiceImpl implements UserDetailsService {

	private final UserRepositoryInternal userRepositoryInternal;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepositoryInternal.findUserByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND)); // this ends up as AuthenticationException
	}
}