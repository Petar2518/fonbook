package rs.ac.bg.fon.reservationservice.repository;


import rs.ac.bg.fon.reservationservice.model.Reservation;
import rs.ac.bg.fon.reservationservice.model.ReservationStatus;
import rs.ac.bg.fon.reservationservice.model.Review;
import rs.ac.bg.fon.reservationservice.util.DataJpaTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("datajpa")
public class ReviewRepositoryTest extends DataJpaTestBase {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReservationRepository reservationRepository;


    @Test
    void createReview_expectIdNotNull() {
        Review review = saveReview();

        Review retrievedReview = reviewRepository.findById(review.getId()).get();

        assertThat(retrievedReview.getId()).isNotNull();
    }

    @Test
    public void getReview_expectCorrectAttributes() {

        Review review = saveReview();

        Review retrievedReview = reviewRepository.findById(review.getId()).get();

        assertThat(retrievedReview.getId()).isNotNull();
        assertEquals(review, retrievedReview);
    }

    @Test
    public void updateReview_expectReviewChanged() {
        Review review = saveReview();

        review.setRating(9.0);
        review.setTitle("Ok");

        Review updatedReview = reviewRepository.save(review);

        assertEquals(review, updatedReview);
    }

    @Test
    void getReviewsByAccommodationId() {
        Reservation reservation1 = createReservation();
        Reservation reservation2 = createReservation();
        reservation1.setCheckInDate(LocalDate.now());
        reservation1.setCheckOutDate(LocalDate.now().plusDays(2));

        Reservation savedReservation1 = reservationRepository.save(reservation1);
        Reservation savedReservation2 = reservationRepository.save(reservation2);

        Review review1 = newReview(savedReservation1);
        Review review2 = newReview(savedReservation2);
        review2.setAccommodationId(2l);

        reviewRepository.save(review1);
        reviewRepository.save(review2);


        Long accommodationId = 1L;
        Page<Review> resultPage = reviewRepository.findByAccommodationId(accommodationId, Pageable.ofSize(5));
        assertThat(resultPage).hasSize(1);
    }

    public Review saveReview() {
        Reservation reservation = createReservation();
        Reservation savedReservation = reservationRepository.save(reservation);


        Review review = newReview(savedReservation);

        reservationRepository.save(reservation);
        reviewRepository.save(review);

        return review;
    }

    Review newReview(Reservation reservation) {
        return Review.builder()
                .id(reservation.getId())
                .reservation(reservation)
                .title("Exceptional")
                .comment("This guest didn't leave a comment")
                .rating(9.00)
                .accommodationId(1L)
                .build();
    }

    public Reservation createReservation() {
        double price = 10.00;
        return Reservation.builder()
                .creationDate(LocalDate.now())
                .currency("eur")
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(5))
                .totalAmount(BigDecimal.valueOf(price))
                .status(ReservationStatus.ACTIVE)
                .numberOfPeople(3)
                .accommodationUnitId(1L)
                .profileId(1L)
                .build();
    }

}
