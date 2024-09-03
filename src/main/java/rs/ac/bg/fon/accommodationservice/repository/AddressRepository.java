package rs.ac.bg.fon.accommodationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.accommodationservice.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
