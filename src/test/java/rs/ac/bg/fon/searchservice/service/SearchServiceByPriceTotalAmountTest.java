package rs.ac.bg.fon.searchservice.service;

import rs.ac.bg.fon.searchservice.model.Accommodation;
import rs.ac.bg.fon.searchservice.model.AccommodationUnit;
import rs.ac.bg.fon.searchservice.model.Price;
import rs.ac.bg.fon.searchservice.repository.AccommodationRepository;
import rs.ac.bg.fon.searchservice.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import util.MongoContainerInitializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Tag("datajpa")
class SearchServiceByPriceTotalAmountTest extends MongoContainerInitializer {

    @Autowired
    WebTestClient webTestClient;


    @Autowired
    PriceRepository priceRepository;
    @Autowired
    AccommodationRepository accommodationRepository;
    private static AccommodationUnit unit;

    @BeforeEach
    void setUp() {


        unit = AccommodationUnit.builder()
                .id(40L)
                .capacity(4)
                .build();

        Price price1 = Price.builder()
                .id(10)
                .dateFrom(LocalDate.of(2025, 5, 1))
                .dateTo(LocalDate.of(2025, 5, 10))
                .amount(BigDecimal.valueOf(30))
                .accommodationUnitId(unit.getId())
                .build();

        Price price2 = Price.builder()
                .id(11)
                .dateFrom(LocalDate.of(2025, 5, 10))
                .dateTo(LocalDate.of(2025, 5, 20))
                .amount(BigDecimal.valueOf(40))
                .accommodationUnitId(unit.getId())
                .build();

        Price price3 = Price.builder()
                .id(12)
                .dateFrom(LocalDate.of(2025, 5, 20))
                .dateTo(LocalDate.of(2025, 5, 30))
                .amount(BigDecimal.valueOf(50))
                .accommodationUnitId(unit.getId())
                .build();

        Price price4 = Price.builder()
                .id(13)
                .dateFrom(LocalDate.of(2025, 5, 30))
                .dateTo(LocalDate.of(2025, 6, 10))
                .amount(BigDecimal.valueOf(45))
                .accommodationUnitId(unit.getId())
                .build();

        Price price5 = Price.builder()
                .id(14)
                .dateFrom(LocalDate.of(2025, 6, 10))
                .dateTo(LocalDate.of(2025, 6, 20))
                .amount(BigDecimal.valueOf(55))
                .accommodationUnitId(unit.getId())
                .build();

        Price price6 = Price.builder()
                .id(14)
                .dateFrom(LocalDate.of(2025, 7, 10))
                .dateTo(LocalDate.of(2025, 7, 20))
                .amount(BigDecimal.valueOf(55))
                .accommodationUnitId(unit.getId())
                .build();


        priceRepository.save(price1);
        priceRepository.save(price2);
        priceRepository.save(price3);
        priceRepository.save(price4);
        priceRepository.save(price5);
        priceRepository.save(price6);

        Set<AccommodationUnit> units = new HashSet<>();
        units.add(unit);


        Accommodation accommodation = Accommodation.builder()
                .id(105)
                .accommodationUnits(units)
                .build();

        accommodationRepository.save(accommodation);

    }

    @Test
    void checkInDateInsidePriceRange() {

        LocalDate checkIn = LocalDate.of(2025, 7, 14);
        LocalDate checkOut = LocalDate.of(2025, 7, 25);

        webTestClient.get()
                .uri("search/?checkIn=" + checkIn + "&checkOut=" + checkOut)
                .exchange()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.content[0].accommodationUnits[0].prices[0].totalAmount").isEqualTo(330.0);
    }

    @Test
    void checkOutDateInsidePriceRange() {

        LocalDate checkIn = LocalDate.of(2025, 7, 5);
        LocalDate checkOut = LocalDate.of(2025, 7, 15);

        webTestClient.get()
                .uri("search/?checkIn=" + checkIn + "&checkOut=" + checkOut)
                .exchange()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.content[0].accommodationUnits[0].prices[0].totalAmount").isEqualTo(275.0);
    }

    @Test
    void bothDatesInsidePriceRange() {

        LocalDate checkIn = LocalDate.of(2025, 7, 12);
        LocalDate checkOut = LocalDate.of(2025, 7, 18);

        //expected ValidForDays 6, Amount 55

        webTestClient.get()
                .uri("search/?checkIn=" + checkIn + "&checkOut=" + checkOut)
                .exchange()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.content[0].accommodationUnits[0].prices[0].totalAmount").isEqualTo(330.0);
    }


    @Test
    void bothDatesOutsidePriceRange() {

        LocalDate checkIn = LocalDate.of(2025, 7, 8);
        LocalDate checkOut = LocalDate.of(2025, 7, 23);

        //expected ValidForDays 10, Amount 55

        webTestClient.get()
                .uri("search/?checkIn=" + checkIn + "&checkOut=" + checkOut)
                .exchange()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.content[0].accommodationUnits[0].prices[0].totalAmount").isEqualTo(550.0);
    }

    @Test
    void checkInEdgeCase() {

        LocalDate checkIn = LocalDate.of(2025, 5, 8);
        LocalDate checkOut = LocalDate.of(2025, 5, 10);

        webTestClient.get()
                .uri("search/?checkIn=" + checkIn + "&checkOut=" + checkOut)
                .exchange()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.content[0].accommodationUnits[0].prices[0].totalAmount").isEqualTo(60.0);
    }


    @Test
    void checkOutEdgeCase() {

        LocalDate checkIn = LocalDate.of(2025, 5, 10);
        LocalDate checkOut = LocalDate.of(2025, 5, 11);

        webTestClient.get()
                .uri("search/?checkIn=" + checkIn + "&checkOut=" + checkOut)
                .exchange()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.content[0].accommodationUnits[0].prices[0].totalAmount").isEqualTo(40.0);
    }


    @Test
    void datesContainTwoPrices() {

        LocalDate checkIn = LocalDate.of(2025, 5, 8);
        LocalDate checkOut = LocalDate.of(2025, 5, 14);

        webTestClient.get()
                .uri("search/?checkIn=" + checkIn + "&checkOut=" + checkOut)
                .exchange()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.content[0].accommodationUnits[0].prices[0].totalAmount").isEqualTo(220.0);
    }

    @Test
    void datesContainFourDates() {

        LocalDate checkIn = LocalDate.of(2025, 5, 5);
        LocalDate checkOut = LocalDate.of(2025, 6, 5);

        webTestClient.get()
                .uri("search/?checkIn=" + checkIn + "&checkOut=" + checkOut)
                .exchange()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.content[0].accommodationUnits[0].prices[0].totalAmount").isEqualTo(1320.0);
    }

    @Test
    void datesAreTheSameAsPriceRange() {

        LocalDate checkIn = LocalDate.of(2025, 5, 1);
        LocalDate checkOut = LocalDate.of(2025, 5, 10);

        webTestClient.get()
                .uri("search/?checkIn=" + checkIn + "&checkOut=" + checkOut)
                .exchange()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.content[0].accommodationUnits[0].prices[0].totalAmount").isEqualTo(270.0);
    }

}