package rs.ac.bg.fon.reservationservice.adapters;

import rs.ac.bg.fon.reservationservice.dto.CreateReviewDto;
import rs.ac.bg.fon.reservationservice.dto.ReviewDto;
import rs.ac.bg.fon.reservationservice.dto.UpdateReviewDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewDtoDomainAdapter {

    Long save(Long reservationId, CreateReviewDto createReviewDto);

    ReviewDto getById(Long reservationId);

    void updateReview(Long reservationId, UpdateReviewDto updateReviewDto);

    List<ReviewDto> getByAccommodationId(Long accommodationId, Pageable pageable);

}
