package org.pizzeria.fabulosa.common.dao.resources;

import org.pizzeria.fabulosa.common.entity.resources.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

}