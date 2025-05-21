package org.pizzeria.fabulosa.common.dao.internal;

import org.pizzeria.fabulosa.common.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoryInternal extends JpaRepository<User, Long> {

	Optional<User> findUserByEmail(String email);

	Boolean existsByEmail(String email);
}
