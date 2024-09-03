package rs.ac.bg.fon.searchservice.repository;

import rs.ac.bg.fon.searchservice.model.Price;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PriceRepository extends MongoRepository<Price, Long> {

    List<Price> findByAccommodationUnitId(Long accommodationUnitId);

}
