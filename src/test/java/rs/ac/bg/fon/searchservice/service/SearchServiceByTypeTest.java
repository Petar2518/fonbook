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
class SearchServiceByTypeTest extends MongoContainerInitializer {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    AccommodationRepository accommodationRepository;

    @BeforeEach
    void setUp() {

        AccommodationUnit unit = AccommodationUnit.builder()
                .id(77L)
                .build();

        Set<AccommodationUnit> units = new HashSet<>();
        units.add(unit);

        Accommodation accommodationHotel1 = Accommodation.builder()
                .id(888)
                .accommodationType(AccommodationType.HOTEL)
                .accommodationUnits(units)
                .build();

        Accommodation accommodationHotel2 = Accommodation.builder()
                .id(889)
                .accommodationType(AccommodationType.HOTEL)
                .accommodationUnits(units)
                .build();

        Accommodation accommodationCottage1 = Accommodation.builder()
                .id(890)
                .accommodationType(AccommodationType.COTTAGE)
                .accommodationUnits(units)
                .build();

        accommodationRepository.save(accommodationHotel1);
        accommodationRepository.save(accommodationHotel2);
        accommodationRepository.save(accommodationCottage1);


    }

    @Test
    void findByTypeHotelTwoExist() {

        String type = String.valueOf(AccommodationType.HOTEL);

        webTestClient.get()
                .uri("search/?type=" + type)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(2);
    }

    @Test
    void findByTypeCottageOneExist() {

        String type = String.valueOf(AccommodationType.COTTAGE);


        webTestClient.get()
                .uri("search/?type=" + type)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1);
    }

    @Test
    void findByTypeApartmentNoneExist() {

        String type = String.valueOf(AccommodationType.APARTMENT);

        webTestClient.get()
                .uri("search/?type=" + type)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(0);

    }

}