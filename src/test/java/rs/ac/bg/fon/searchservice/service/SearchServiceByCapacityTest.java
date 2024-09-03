package rs.ac.bg.fon.searchservice.service;

import rs.ac.bg.fon.searchservice.model.Accommodation;
import rs.ac.bg.fon.searchservice.model.AccommodationType;
import rs.ac.bg.fon.searchservice.model.AccommodationUnit;
import rs.ac.bg.fon.searchservice.repository.AccommodationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import util.MongoContainerInitializer;

import java.util.HashSet;
import java.util.Set;

@Tag("datajpa")
class SearchServiceByCapacityTest extends MongoContainerInitializer {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    AccommodationRepository accommodationRepository;

    @BeforeEach
    void setUp() {

        AccommodationUnit unit1 = AccommodationUnit.builder()
                .id(40L)
                .capacity(4)
                .build();

        AccommodationUnit unit2 = AccommodationUnit.builder()
                .id(41L)
                .capacity(4)
                .build();

        AccommodationUnit unit3 = AccommodationUnit.builder()
                .id(42L)
                .capacity(5)
                .build();

        AccommodationUnit unit4 = AccommodationUnit.builder()
                .id(43L)
                .capacity(6)
                .build();

        AccommodationUnit unit5 = AccommodationUnit.builder()
                .id(44L)
                .capacity(7)
                .build();

        AccommodationUnit unit6 = AccommodationUnit.builder()
                .id(45L)
                .capacity(8)
                .build();

        Set<AccommodationUnit> units = new HashSet<>();

        units.add(unit1);
        units.add(unit2);
        units.add(unit3);
        units.add(unit4);
        units.add(unit5);
        units.add(unit6);


        Accommodation accommodation = Accommodation.builder()
                .id(120)
                .name("tempName")
                .accommodationType(AccommodationType.HOTEL)
                .accommodationUnits(units)
                .build();

        accommodationRepository.save(accommodation);

    }

    @Test
    void findByCapacityTwoExist() {

        String capacity = "4";

        webTestClient.get()
                .uri("search/?capacity=" + capacity + "&page=0&size=10")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1)
                .jsonPath("$.content[0].accommodationUnits[0].id").isEqualTo("40")
                .jsonPath("$.content[0].accommodationUnits[1].id").isEqualTo("41");
    }

    @Test
    void findByCapacityOneExist() {

        String capacity = "5";

        webTestClient.get()
                .uri("search/?capacity=" + capacity + "&page=0&size=10")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1)
                .jsonPath("$.content[0].accommodationUnits[0].id").isEqualTo("42");
    }


    @Test
    void findByCapacityNoneExist() {

        String capacity = "55";

        webTestClient.get()
                .uri("search/?capacity=" + capacity + "&page=0&size=10")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(0);
    }
}