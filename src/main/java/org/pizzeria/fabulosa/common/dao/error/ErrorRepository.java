package org.pizzeria.fabulosa.common.dao.error;

import org.pizzeria.fabulosa.common.entity.error.APIError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorRepository extends JpaRepository<APIError, Long> {
}
