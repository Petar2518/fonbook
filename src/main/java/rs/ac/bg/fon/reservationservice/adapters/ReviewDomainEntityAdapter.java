package rs.ac.bg.fon.reservationservice.adapters;

import rs.ac.bg.fon.reservationservice.domain.ReviewDomain;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewDomainEntityAdapter {

    Long save(ReviewDomain reviewDomain, Long accommodationId);

    void updateReview(ReviewDomain reviewDomain);


    Optional<ReviewDomain> getById(Long reservationId);

    List<ReviewDomain> getByAccommodationId(Long accommodationId, Pageable pageable);
}
