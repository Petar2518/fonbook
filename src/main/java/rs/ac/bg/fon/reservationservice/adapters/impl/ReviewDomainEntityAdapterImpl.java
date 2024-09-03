package rs.ac.bg.fon.reservationservice.adapters.impl;

import rs.ac.bg.fon.reservationservice.adapters.ReviewDomainEntityAdapter;
import rs.ac.bg.fon.reservationservice.domain.ReviewDomain;
import rs.ac.bg.fon.reservationservice.mapper.ReviewMapper;
import rs.ac.bg.fon.reservationservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewDomainEntityAdapterImpl implements ReviewDomainEntityAdapter {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public Long save(ReviewDomain reviewDomain, Long accommodationId) {
        return reviewRepository
                .save(reviewMapper
                        .fromDomainToEntity(reviewDomain, accommodationId)).getId();
    }

    @Override
    public void updateReview(ReviewDomain reviewDomain) {
         reviewRepository
                .save(reviewMapper
                        .fromDomainToEntity(reviewDomain));
    }

    @Override
    public Optional<ReviewDomain> getById(Long reservationId) {

        return reviewRepository
                .findById(reservationId)
                .map(reviewMapper::fromEntityToDomain);
    }

    @Override
    public List<ReviewDomain> getByAccommodationId(Long accommodationId, Pageable pageable) {
        return reviewRepository.findByAccommodationId(accommodationId, pageable)
                .get()
                .map(reviewMapper::fromEntityToDomain)
                .toList();
    }


}
