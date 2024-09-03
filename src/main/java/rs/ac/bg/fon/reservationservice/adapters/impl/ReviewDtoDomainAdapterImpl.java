package rs.ac.bg.fon.reservationservice.adapters.impl;

import rs.ac.bg.fon.reservationservice.adapters.ReviewDtoDomainAdapter;
import rs.ac.bg.fon.reservationservice.domain.UpdateReviewDomain;
import rs.ac.bg.fon.reservationservice.dto.CreateReviewDto;
import rs.ac.bg.fon.reservationservice.dto.ReviewDto;
import rs.ac.bg.fon.reservationservice.dto.UpdateReviewDto;
import rs.ac.bg.fon.reservationservice.mapper.ReviewMapper;
import rs.ac.bg.fon.reservationservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewDtoDomainAdapterImpl implements ReviewDtoDomainAdapter {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @Override
    public Long save(Long reservationId, CreateReviewDto createReviewDto) {
        return reviewService
                .save(reservationId, reviewMapper
                        .fromCreateDtoToDomain(createReviewDto));

    }

    @Override
    public ReviewDto getById(Long reservationId) {
        return reviewMapper
                .fromDomainToDto(reviewService
                        .getById(reservationId));
    }

    @Override
    public void updateReview(Long reservationId, UpdateReviewDto updateReviewDto) {
        UpdateReviewDomain updateReviewDomain = reviewMapper.fromUpdateReviewDtoToUpdateReviewDomain(updateReviewDto);
        reviewService.updateReview(reservationId, updateReviewDomain);

    }

    @Override
    public List<ReviewDto> getByAccommodationId(Long accommodationId, Pageable pageable) {
        return reviewService.getByAccommodationId(accommodationId, pageable)
                .stream()
                .map(reviewMapper::fromDomainToDto)
                .toList();
    }


}
