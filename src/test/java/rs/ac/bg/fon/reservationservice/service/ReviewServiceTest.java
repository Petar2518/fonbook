package rs.ac.bg.fon.reservationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import rs.ac.bg.fon.reservationservice.adapters.ReviewDomainEntityAdapter;
import rs.ac.bg.fon.reservationservice.domain.ReviewDomain;
import rs.ac.bg.fon.reservationservice.domain.UpdateReviewDomain;
import rs.ac.bg.fon.reservationservice.exceptions.ResourceNotFoundException;
import rs.ac.bg.fon.reservationservice.feignclient.AccommodationClient;
import rs.ac.bg.fon.reservationservice.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    ReviewDomainEntityAdapter reviewDomainEntityAdapter;
    @Mock
    ReservationService reservationService;

    @Mock
    AccommodationClient accommodationClient;

    @InjectMocks
    ReviewServiceImpl reviewServiceImpl;

    @BeforeEach
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        reviewServiceImpl = new ReviewServiceImpl(reviewDomainEntityAdapter, objectMapper, accommodationClient, reservationService);
    }


    @Test
    void save_expectSuccess() {
        ReviewDomain reviewDomain = newReviewDomain();

        reviewDomainEntityAdapter.save(reviewDomain, 1L);

        verify(reviewDomainEntityAdapter, times(1)).save(reviewDomain, 1L);
    }

    @Test
    void getByReservationId_expectSuccess() {
        ReviewDomain expectedReviewDomain = newReviewDomain();
        when(reviewDomainEntityAdapter
                .getById(expectedReviewDomain.getId()))
                .thenReturn(Optional.of(expectedReviewDomain));

        ReviewDomain actualReviewDomain = reviewServiceImpl
                .getById(expectedReviewDomain.getId());

        assertEquals(expectedReviewDomain, actualReviewDomain);
    }

    @Test
    void updateReview_expectSuccess() {
        Long reservationId = 1L;
        ReviewDomain existingReviewDomain = newReviewDomain();
        when(reviewDomainEntityAdapter
                .getById(existingReviewDomain.getId()))
                .thenReturn(Optional.of(existingReviewDomain));

        UpdateReviewDomain updatedReviewDomain = newUpdateReviewDomain();


        reviewServiceImpl.updateReview(reservationId, updatedReviewDomain);


        verify(reviewDomainEntityAdapter).updateReview(existingReviewDomain);


        assertEquals("Updated comment", existingReviewDomain.getComment());
        assertEquals("Updated title", existingReviewDomain.getTitle());
    }

    @Test
    void getById_ifNotFound_expectException() {
        Long id = 1L;

        when(reviewDomainEntityAdapter
                .getById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewServiceImpl.getById(id));
        verify(reviewDomainEntityAdapter, times(1)).getById(id);
    }

    @Test
    void getByAccommodationId_Success() {
        Long accommodationId = 1L;
        ReviewDomain review = newReviewDomain();
        when(reviewDomainEntityAdapter.getByAccommodationId(accommodationId, null)).thenReturn(List.of(review));

        List<ReviewDomain> reviews = reviewServiceImpl.getByAccommodationId(accommodationId, null);

        assertThat(reviews).isNotEmpty();
        assertThat(reviews).contains(review);
    }

    @Test
    void getByAccommodationId_Empty() {
        Long accommodationId = 1L;
        when(reviewDomainEntityAdapter.getByAccommodationId(accommodationId, null)).thenReturn(List.of());

        List<ReviewDomain> reviews = reviewServiceImpl.getByAccommodationId(accommodationId, null);

        assertThat(reviews).isEmpty();
    }


    ReviewDomain newReviewDomain() {
        return ReviewDomain.builder()
                .id(1L)
                .title("Exceptional")
                .comment("This guest didn't leave a comment")
                .rating(8.00)
                .accommodationId(1L)
                .build();
    }

    UpdateReviewDomain newUpdateReviewDomain() {
        return UpdateReviewDomain.builder()
                .title("Updated title")
                .comment("Updated comment")
                .build();
    }


}
