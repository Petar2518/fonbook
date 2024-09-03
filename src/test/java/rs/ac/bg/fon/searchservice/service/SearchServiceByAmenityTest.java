package rs.ac.bg.fon.searchservice.service;

import rs.ac.bg.fon.searchservice.model.Accommodation;
import rs.ac.bg.fon.searchservice.model.AccommodationUnit;
import rs.ac.bg.fon.searchservice.model.Amenity;
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
class SearchServiceByAmenityTest extends MongoContainerInitializer {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    AccommodationRepository accommodationRepository;


    @BeforeEach
    void setUp() {


        Amenity spa = Amenity.builder()
                .id(4)
                .amenity("Spa")
                .build();

        Amenity casino = Amenity.builder()
                .id(4)
                .amenity("Casino")
                .build();

        Amenity gym = Amenity.builder()
                .id(4)
                .amenity("Gym")
                .build();

        Set<Amenity> amenities = new HashSet<>();

        amenities.add(spa);
        amenities.add(casino);
        amenities.add(gym);

        AccommodationUnit unit = AccommodationUnit.builder()
                .id(77L)
                .build();

        Set<AccommodationUnit> units = new HashSet<>();
        units.add(unit);

        Accommodation accommodation = Accommodation.builder()
                .id(137)
                .accommodationUnits(units)
                .amenities(amenities)
                .build();

        accommodationRepository.save(accommodation);
    }

    @Test
    void findByOneAmenityGood() {

        String amenity = "Spa";

        webTestClient.get()
                .uri("search/?amenities=" + amenity + "&page=0&size=10")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1);
    }

    @Test
    void findByTwoAmenitiesBad() {

        String firstAmenity = "Spa";
        String secondAmenity = "Library";

        webTestClient.get()
                .uri("search/?amenities=" + firstAmenity + "&amenities=" + secondAmenity + "&page=0&size=10")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(0);
    }


    @Test
    void findByTwoAmenitiesGood() {

        String firstAmenity = "Spa";
        String secondAmenity = "Gym";


        webTestClient.get()
                .uri("search/?amenities=" + firstAmenity + "&amenities=" + secondAmenity + "&page=0&size=10")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1);

    }


}