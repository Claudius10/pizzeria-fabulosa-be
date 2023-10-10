package PizzaApp.api.services.user.address;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import PizzaApp.api.entity.user.Address;
import PizzaApp.api.repos.user.address.AddressRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

	private final AddressRepository addressRepository;

	public AddressServiceImpl(AddressRepository addressRepository) {
		this.addressRepository = addressRepository;
	}

	@Override
	public Address findReference(String id) {
		return addressRepository.findReference(id);
	}

	@Override
	public Optional<Address> find(Address address) {
		if (address.getId() != null) {
			return Optional.ofNullable(findReference(String.valueOf(address.getId())));
		} else {
			return addressRepository.findAddress(address);
		}
	}

	@Override
	public List<Address> findAllByUserId(String id) {
		return addressRepository.findAllByUserId(id);
	}

	@Override
	public Long findUserAddressListSize(String id) {
		return addressRepository.findUserAddressListSize(id);
	}
}
