package rs.ac.bg.fon.reservationservice.repository;

import rs.ac.bg.fon.reservationservice.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByAccommodationId(Long accommodationId, Pageable pageable);
}
