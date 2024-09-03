package rs.ac.bg.fon.accommodationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.accommodationservice.model.Amenity;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}
