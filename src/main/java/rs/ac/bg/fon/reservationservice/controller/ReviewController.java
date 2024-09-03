package rs.ac.bg.fon.reservationservice.controller;

import rs.ac.bg.fon.reservationservice.adapters.ReviewDtoDomainAdapter;
import rs.ac.bg.fon.reservationservice.dto.CreateReviewDto;
import rs.ac.bg.fon.reservationservice.dto.ReviewDto;
import rs.ac.bg.fon.reservationservice.dto.UpdateReviewDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewDtoDomainAdapter reviewDtoDomainAdapter;

    @PostMapping("reservations/{reservationId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createReview(@PathVariable Long reservationId, @RequestBody @Valid CreateReviewDto createReviewDto) {
        return reviewDtoDomainAdapter.save(reservationId, createReviewDto);
    }

    @GetMapping("reservations/{reservationId}/reviews")
    public ReviewDto getReviewFromReservation(@PathVariable Long reservationId) {
        return reviewDtoDomainAdapter.getById(reservationId);
    }

    @PatchMapping("reservations/{reservationId}/reviews")
    public void updateReview(@PathVariable Long reservationId, @RequestBody @Valid UpdateReviewDto updateReviewDto) {
        reviewDtoDomainAdapter.updateReview(reservationId, updateReviewDto);
    }

    @GetMapping("accommodations/{accommodationId}/reviews")
    public List<ReviewDto> getByAccommodationId(
            @PathVariable Long accommodationId,
            @PageableDefault(
                    sort = "id",
                    page = 0,
                    size = 5,
                    direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return reviewDtoDomainAdapter.getByAccommodationId(accommodationId, pageable);
    }

}
