package rs.ac.bg.fon.searchservice.service;

import rs.ac.bg.fon.searchservice.model.Accommodation;
import rs.ac.bg.fon.searchservice.model.AccommodationUnit;
import rs.ac.bg.fon.searchservice.repository.AccommodationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import util.MongoContainerInitializer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
@Tag("datajpa")
class SearchServiceByNameTest extends MongoContainerInitializer {

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

        List<String> hotelList = Arrays.asList
                (
                        "Sunset Bliss Resort",
                        "Ocean View Hotel",
                        "Mountain Retreat Lodge",
                        "Serenity Spa & Resort",
                        "Golden Sands Beach Hotel",
                        "Eternal City Suites",
                        "Majestic Peaks Inn",
                        "Tranquil Oasis Retreat",
                        "Whispering Pines Lodge",
                        "Azure Skyline Resort",
                        "Regal Plaza Hotel",
                        "Mystic Meadows Inn",
                        "Cascading Waterfalls Retreat",
                        "Starlight Horizon Hotel",
                        "Pristine Paradise Retreat",
                        "Auburn Autumn Inn",
                        "Cerulean Cascade Resort",
                        "Verdant Valley Lodge",
                        "Solitude Springs Hotel",
                        "Ivory Iceberg Inn",
                        "Halcyon Harbor Retreat",
                        "Silver Symphony Lodge"
                );

        IntStream.range(0, hotelList.size())
                .mapToObj(index -> Accommodation.builder()
                        .id(index)
                        .name(hotelList.get(index))
                        .accommodationUnits(units)
                        .build())
                .forEach(accommodationRepository::save);


    }

    @Test
    void findByNameOne() {

        String nameQuery = "Harbor";

        webTestClient.get()
                .uri("search/?name=" + nameQuery)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1)
                .jsonPath("$.content[0].name").isEqualTo("Halcyon Harbor Retreat");
    }

    @Test
    void findByNameNone() {

        String nameQuery = "testNone";

        webTestClient.get()
                .uri("search/?name=" + nameQuery)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(0);

    }

    @Test
    void findByNameCouple() {

        String nameQuery = "reSoR";

        webTestClient.get()
                .uri("search/?name=" + nameQuery)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(4)
                .jsonPath("$.content[0].name").isEqualTo("Sunset Bliss Resort")
                .jsonPath("$.content[1].name").isEqualTo("Serenity Spa & Resort")
                .jsonPath("$.content[2].name").isEqualTo("Azure Skyline Resort")
                .jsonPath("$.content[3].name").isEqualTo("Cerulean Cascade Resort");

    }

}