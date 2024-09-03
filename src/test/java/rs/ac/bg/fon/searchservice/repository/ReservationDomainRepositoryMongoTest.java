package rs.ac.bg.fon.searchservice.repository;

import rs.ac.bg.fon.searchservice.model.Reservation;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import util.MongoContainerInitializer;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("datajpa")
class ReservationDomainRepositoryMongoTest extends MongoContainerInitializer {


    @Autowired
    ReservationRepository reservationRepository;

    @Test
    void saveAndFindById() {

        Reservation reservation = Reservation.builder()
                .id(2)
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(5)).build();


        reservationRepository.save(reservation);

        Reservation retrievedReservation =
                reservationRepository.findById(reservation.getId()).orElse(null);

        assertThat(retrievedReservation).isNotNull();
        assertThat(retrievedReservation).usingRecursiveComparison().isEqualTo(reservation);

    }
}