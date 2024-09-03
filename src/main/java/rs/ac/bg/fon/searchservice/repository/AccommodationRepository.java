package rs.ac.bg.fon.searchservice.repository;

import rs.ac.bg.fon.searchservice.model.Accommodation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AccommodationRepository extends MongoRepository<Accommodation, Long> {

    Optional<Accommodation> findByAccommodationUnitsId(@Param("id") Long id);

}
