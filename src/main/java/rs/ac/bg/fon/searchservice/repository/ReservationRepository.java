package rs.ac.bg.fon.searchservice.repository;

import rs.ac.bg.fon.searchservice.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, Long> {

}
