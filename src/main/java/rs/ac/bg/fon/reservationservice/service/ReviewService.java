package rs.ac.bg.fon.reservationservice.service;

import rs.ac.bg.fon.reservationservice.domain.ReviewDomain;
import rs.ac.bg.fon.reservationservice.domain.UpdateReviewDomain;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    Long save(Long reservationId, ReviewDomain reviewDomain);

    ReviewDomain getById(Long reservationId);

    void updateReview(Long reservationId, UpdateReviewDomain updateReviewDomain);

    List<ReviewDomain> getByAccommodationId(Long accommodationId, Pageable pageable);
}
