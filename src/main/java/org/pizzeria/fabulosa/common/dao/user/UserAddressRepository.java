package org.pizzeria.fabulosa.common.dao.user;

import org.pizzeria.fabulosa.common.entity.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserAddressRepository extends JpaRepository<Address, Long> {

	@Query("select user.addressList from User user where user.id = :userId")
	Set<Address> findUserAddressListById(Long userId);
}
