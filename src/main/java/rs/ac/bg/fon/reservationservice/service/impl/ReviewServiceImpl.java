package rs.ac.bg.fon.reservationservice.service.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import rs.ac.bg.fon.reservationservice.adapters.ReviewDomainEntityAdapter;
import rs.ac.bg.fon.reservationservice.domain.ReservationDomain;
import rs.ac.bg.fon.reservationservice.domain.ReviewDomain;
import rs.ac.bg.fon.reservationservice.domain.UpdateReviewDomain;
import rs.ac.bg.fon.reservationservice.exceptions.MapperException;
import rs.ac.bg.fon.reservationservice.exceptions.ResourceNotFoundException;
import rs.ac.bg.fon.reservationservice.feignclient.AccommodationClient;
import rs.ac.bg.fon.reservationservice.service.ReservationService;
import rs.ac.bg.fon.reservationservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDomainEntityAdapter reviewDomainEntityAdapter;
    private final ObjectMapper objectMapper;
    private final AccommodationClient accommodationClient;
    private final ReservationService reservationService;


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public Long save(Long reservationId, ReviewDomain reviewDomain) {

        ReservationDomain reservationDomain = reservationService.getById(reservationId);
        JsonNode accommodationUnitDto = accommodationClient.getAccommodationUnitById(reservationDomain.getAccommodationUnitId());
        Long accommodationId = accommodationUnitDto.get("accommodation").get("id").asLong();

        reviewDomain.setId(reservationId);

        return reviewDomainEntityAdapter.save(reviewDomain, accommodationId);
    }

    @Override
    public ReviewDomain getById(Long reservationId) {
        return reviewDomainEntityAdapter
                .getById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "reservation id", reservationId));

    }

    @Override
    public void updateReview(Long reservationId, UpdateReviewDomain updatedReviewDomain) {

        ReviewDomain existingReviewDomain = this
                .getById(reservationId);

        try {
            objectMapper.updateValue(existingReviewDomain, updatedReviewDomain);
        } catch (JsonMappingException e) {
            throw new MapperException(e.getMessage());
        }
        reviewDomainEntityAdapter.updateReview(existingReviewDomain);

    }

    @Override
    public List<ReviewDomain> getByAccommodationId(Long accommodationId, Pageable pageable) {
        return reviewDomainEntityAdapter.getByAccommodationId(accommodationId, pageable);
    }
}
