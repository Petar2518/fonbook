package rs.ac.bg.fon.searchservice.mapper;

import rs.ac.bg.fon.searchservice.domain.ReservationDomain;
import rs.ac.bg.fon.searchservice.model.Reservation;
import rs.ac.bg.fon.searchservice.model.ReservationStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("springboot")
class ReservationMapperTest {

    @Autowired
    ReservationMapper reservationMapper;

    static Reservation reservation;

    @BeforeAll
    static void setUp() {

        reservation = Reservation.builder()
                .id(56)
                .checkInDate(LocalDate.now().plusDays(5))
                .checkOutDate(LocalDate.now().plusDays(9))
                .reservationStatus(ReservationStatus.ACTIVE)
                .build();
    }


    @Test
    void entityToDomain() {


        ReservationDomain mappedReservationDomain = reservationMapper.entityToDomain(reservation);

        assertThat(reservation.getId()).isEqualTo(mappedReservationDomain.getId());
        assertThat(reservation.getCheckInDate()).isEqualTo(mappedReservationDomain.getCheckInDate());
        assertThat(reservation.getCheckOutDate()).isEqualTo(mappedReservationDomain.getCheckOutDate());
        assertThat(reservation.getReservationStatus()).isEqualTo(mappedReservationDomain.getReservationStatus());


    }

    @Test
    void entitiesToDomains() {

        List<Reservation> reservations = Collections.singletonList(reservation);
        List<ReservationDomain> reservationDomains = reservationMapper.entitiesToDomains(reservations);

        assertThat(reservations.size()).isEqualTo(reservationDomains.size());
        assertThat(reservations.getFirst().getId()).isEqualTo(reservationDomains.getFirst().getId());
        assertThat(reservations.getFirst().getCheckInDate()).isEqualTo(reservationDomains.getFirst().getCheckInDate());
        assertThat(reservations.getFirst().getCheckOutDate()).isEqualTo(reservationDomains.getFirst().getCheckOutDate());
        assertThat(reservations.getFirst().getReservationStatus()).isEqualTo(reservationDomains.getFirst().getReservationStatus());


    }

    @Test
    void domainToEntity() {

        ReservationDomain reservationDomain = ReservationDomain.builder()
                .id(56)
                .checkInDate(LocalDate.now().plusDays(5))
                .checkOutDate(LocalDate.now().plusDays(9))
                .reservationStatus(ReservationStatus.ACTIVE)
                .build();

        Reservation result = reservationMapper.domainToEntity(reservationDomain);

        assertThat(result).usingRecursiveComparison().isEqualTo(reservation);
    }
}