package rs.ac.bg.fon.reservationservice.repository;

import rs.ac.bg.fon.reservationservice.model.Reservation;
import rs.ac.bg.fon.reservationservice.model.ReservationStatus;
import rs.ac.bg.fon.reservationservice.util.DataJpaTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("datajpa")
public class ReservationRepositoryTest extends DataJpaTestBase {

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void findReservationBetweenTwoDates_expectSuccess() {
        Reservation reservation = createReservation();
        Reservation reservation2 = createReservation();

        reservation2.setCheckInDate(LocalDate.now().plusDays(5));
        reservation2.setCheckOutDate(LocalDate.now().plusDays(10));

        reservationRepository.save(reservation);
        reservationRepository.save(reservation2);

        Page<Reservation> request1 = reservationRepository.findReservationsBetweenDateRangeUsingProfileId(LocalDate.now(), reservation2.getCheckOutDate(), 1L, Pageable.ofSize(5));

        assertThat(request1.getContent().size()).isEqualTo(2);

    }

    @Test
    void findReservationBetweenTwoDatesByUnitIds_expectSuccess() {
        Reservation reservation = createReservation();
        Reservation reservation2 = createReservation();

        reservation2.setCheckInDate(LocalDate.now().plusDays(5));
        reservation2.setCheckOutDate(LocalDate.now().plusDays(10));
        Reservation reservation3 = createReservation();
        reservation3.setCheckInDate(LocalDate.now().plusDays(10));
        reservation3.setCheckOutDate(LocalDate.now().plusDays(12));
        reservation3.setAccommodationUnitId(2L);

        reservationRepository.save(reservation);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);

        Page<Reservation> request1 =
                reservationRepository.findReservationsBetweenDateRangeUsingUnitIds(
                        LocalDate.now(),
                        reservation2.getCheckOutDate(),
                        List.of(1L),
                        Pageable.ofSize(5));

        assertThat(request1.getContent().size()).isEqualTo(2);
    }


    @Test
    void createReservation_expectIdNotNull() {
        Reservation reservation = createReservation();

        reservationRepository.save(reservation);

        Reservation retrievedReservation = reservationRepository.findById(reservation.getId()).get();

        assertThat(retrievedReservation.getId()).isNotNull();
    }

    @Test
    public void getReservation_expectCorrectAttributes() {

        Reservation reservation = createReservation();

        reservationRepository.save(reservation);

        Reservation retrievedReservation = reservationRepository.findById(reservation.getId()).get();

        assertThat(retrievedReservation.getId()).isNotNull();
        assertEquals(reservation, retrievedReservation);
    }

    @Test
    public void updateReservation_expectReservationChanged() {
        Reservation reservation = createReservation();

        reservationRepository.save(reservation);

        reservation.setStatus(ReservationStatus.CANCELED);
        reservation.setCreationDate(LocalDate.of(LocalDate.EPOCH.getYear(), 12, 12));

        Reservation updatedReservation = reservationRepository.save(reservation);

        assertEquals(reservation, updatedReservation);
    }

    @Test
    public void getListOfReservations_expectNotEmptyList() {

        Reservation reservation1 = createReservation();
        Reservation reservation2 = createReservation();

        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);

        List<Reservation> reservations = reservationRepository.findAll();

        assertThat(reservations.size()).isGreaterThan(0);

    }

    @Test
    public void deleteReservation_expectReservationDeleted() {
        Reservation reservation = createReservation();

        reservationRepository.save(reservation);
        reservationRepository.deleteById(reservation.getId());

        Optional<Reservation> deletedReservation = reservationRepository.findById(reservation.getId());

        assertThat(deletedReservation).isEmpty();
    }

    @Test
    public void findIfReservationExistsBetweenDates_returnNotEmptyList() {
        Reservation reservation = createReservation();

        reservationRepository.save(reservation);

        assertThat(occupyingDates()).isNotEmpty();
        assertThat(betweenDates()).isNotEmpty();
        assertThat(checkInIsBeforeExistingReservationCheckOutIsBetween()).isNotEmpty();
        assertThat(checkOutIsAfterExistingReservationCheckInIsBetween()).isNotEmpty();
        assertThat(availableDates()).isEmpty();

    }

    public List<Reservation> checkOutIsAfterExistingReservationCheckInIsBetween() {
        return reservationRepository
                .findAllExistingReservationsForDates(LocalDate.now().plusDays(4), LocalDate.now().plusDays(20), 1L);
    }

    public List<Reservation> checkInIsBeforeExistingReservationCheckOutIsBetween() {
        return reservationRepository
                .findAllExistingReservationsForDates(LocalDate.now(), LocalDate.now().plusDays(4), 1L);
    }

    public List<Reservation> betweenDates() {
        return reservationRepository
                .findAllExistingReservationsForDates(LocalDate.now().plusDays(1), LocalDate.now().plusDays(4), 1L);

    }

    public List<Reservation> occupyingDates() {
        return reservationRepository
                .findAllExistingReservationsForDates(LocalDate.now(), LocalDate.now().plusDays(20), 1L);

    }

    public List<Reservation> availableDates() {
        return reservationRepository
                .findAllExistingReservationsForDates(LocalDate.now().plusDays(20), LocalDate.now().plusDays(25), 1L);

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