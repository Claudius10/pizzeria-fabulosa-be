package org.pizzeria.fabulosa.common.dao.user;

import org.pizzeria.fabulosa.common.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}