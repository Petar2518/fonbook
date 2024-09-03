package rs.ac.bg.fon.searchservice.service;

import rs.ac.bg.fon.searchservice.model.*;
import rs.ac.bg.fon.searchservice.repository.AccommodationRepository;
import rs.ac.bg.fon.searchservice.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import util.MongoContainerInitializer;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Tag("datajpa")
class SearchServiceByDateTest extends MongoContainerInitializer {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    AccommodationRepository accommodationRepository;
    private static AccommodationUnit unit;

    @BeforeEach
    void setUp() {


        unit = AccommodationUnit.builder()
                .id(40L)
                .capacity(4)
                .build();

        AccommodationUnit unit1 = AccommodationUnit.builder()
                .id(41L)
                .capacity(4)
                .build();


        Reservation reservation1 = Reservation.builder()
                .id(55)
                .reservationStatus(ReservationStatus.ACTIVE)
                .checkInDate(LocalDate.of(2025, 5, 1))
                .checkOutDate(LocalDate.of(2025, 5, 5))
                .accommodationUnitId(unit.getId())
                .build();

        Reservation reservation2 = Reservation.builder()
                .id(56)
                .reservationStatus(ReservationStatus.ACTIVE)
                .checkInDate(LocalDate.of(2025, 5, 10))
                .checkOutDate(LocalDate.of(2025, 5, 15))
                .accommodationUnitId(unit.getId())
                .build();

        Reservation reservation3 = Reservation.builder()
                .id(57)
                .reservationStatus(ReservationStatus.ACTIVE)
                .checkInDate(LocalDate.of(2025, 5, 18))
                .checkOutDate(LocalDate.of(2025, 6, 5))
                .accommodationUnitId(unit.getId())
                .build();

        Reservation reservation4 = Reservation.builder()
                .id(58)
                .reservationStatus(ReservationStatus.ACTIVE)
                .checkInDate(LocalDate.of(2025, 4, 18))
                .checkOutDate(LocalDate.of(2025, 9, 5))
                .accommodationUnitId(unit1.getId())
                .build();


        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);
        reservationRepository.save(reservation4);

        Set<AccommodationUnit> units = new HashSet<>();
        units.add(unit);
        units.add(unit1);

        Accommodation accommodation = Accommodation.builder()
                .id(105)
                .accommodationType(AccommodationType.HOTEL)
                .accommodationUnits(units)
                .build();

        accommodationRepository.save(accommodation);

    }


    @Test
    void findByDateCheckInInsideReserved() {

        LocalDate checkIn = LocalDate.of(2025, 5, 4);
        LocalDate checkOut = LocalDate.of(2025, 5, 9);

        webTestClient.get()
                .uri("search/?checkIn=" + checkIn + "&checkOut=" + checkOut)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(0);
    }

    @Test
    void findByDateCheckOutInsideReserved() {

        LocalDate checkIn = LocalDate.of(2025, 5, 9);
        LocalDate checkOut = LocalDate.of(2025, 5, 11);

        webTestClient.get()
                .uri("search/?checkIn=" + checkIn + "&checkOut=" + checkOut)
                .exchange()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.numberOfElements").isEqualTo(0);
    }

    @Test
    void findByDateCheckInAndCheckOutOverlapReserved() {

        LocalDate checkIn = LocalDate.of(2025, 5, 6);
        LocalDate checkOut = LocalDate.of(2025, 5, 16);


        webTestClient.get()
                .uri("search/?checkIn=" + checkIn + "&checkOut=" + checkOut)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(0);
    }

    @Test
    void findByDateCheckInAndCheckOutGoodDate() {

        LocalDate checkIn = LocalDate.of(2025, 5, 6);
        LocalDate checkOut = LocalDate.of(2025, 5, 9);

        webTestClient.get()
                .uri("search/?checkIn=" + checkIn + "&checkOut=" + checkOut)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1)
                .jsonPath("$.content[0].accommodationUnits[0].id").isEqualTo(unit.getId());
    }

}