package org.pizzeria.fabulosa.repos.error;

import org.pizzeria.fabulosa.entity.error.Error;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorRepository extends JpaRepository<Error, Long> {
}
