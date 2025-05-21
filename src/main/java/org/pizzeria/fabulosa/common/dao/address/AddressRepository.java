package org.pizzeria.fabulosa.common.dao.address;

import org.pizzeria.fabulosa.common.entity.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}