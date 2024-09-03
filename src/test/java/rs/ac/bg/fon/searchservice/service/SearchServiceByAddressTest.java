package rs.ac.bg.fon.searchservice.service;

import rs.ac.bg.fon.searchservice.model.Accommodation;
import rs.ac.bg.fon.searchservice.model.AccommodationType;
import rs.ac.bg.fon.searchservice.model.AccommodationUnit;
import rs.ac.bg.fon.searchservice.model.Address;
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
class SearchServiceByAddressTest extends MongoContainerInitializer {

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

        Address address1 = Address.builder()
                .country("Germany")
                .city("Berlin")
                .build();

        Address address2 = Address.builder()
                .country("Germany")
                .city("Munich")
                .build();

        Address address3 = Address.builder()
                .country("Serbia")
                .city("Belgrade")
                .build();


        Accommodation accommodation1 = Accommodation.builder()
                .id(88)
                .name("asdasda")
                .address(address1)
                .accommodationType(AccommodationType.HOTEL)
                .accommodationUnits(units)
                .build();

        Accommodation accommodation2 = Accommodation.builder()
                .id(89)
                .address(address2)
                .accommodationUnits(units)
                .accommodationType(AccommodationType.HOTEL)
                .build();

        Accommodation accommodation3 = Accommodation.builder()
                .id(90)
                .address(address3)
                .accommodationUnits(units)
                .accommodationType(AccommodationType.HOTEL)
                .build();

        accommodationRepository.save(accommodation1);
        accommodationRepository.save(accommodation2);
        accommodationRepository.save(accommodation3);

    }

    @Test
    void findByCityOneExist() {

        String city = "elgr"; //Belgrade


        webTestClient.get()
                .uri("search/?city=" + city + "&page=0&size=10")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1)
                .jsonPath("$.content[0].id").isNotEmpty()
                .jsonPath("$.content[0].address.city").isEqualTo("Belgrade");
    }

    @Test
    void findByCityNoneExist() {

        String city = "badQuery";

        webTestClient.get()
                .uri("search/?city=" + city + "&page=0&size=10")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(0);

    }

    @Test
    void findByCountryOneExist() {

        String country = "Bia"; //Serbia

        webTestClient.get()
                .uri("search/?country=" + country + "&page=0&size=10")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1)
                .jsonPath("$.content[0].id").isNotEmpty()
                .jsonPath("$.content[0].address.country").isEqualTo("Serbia");

    }

    @Test
    void findByCountryTwoExists() {

        String country = "erMa"; //Germany

        webTestClient.get()
                .uri("search/?country=" + country + "&page=0&size=10")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(2)
                .jsonPath("$.content[0].id").isNotEmpty()
                .jsonPath("$.content[1].id").isNotEmpty()
                .jsonPath("$.content[0].address.country").isEqualTo("Germany");

    }

    @Test
    void findByCountryNoneExists() {

        String country = "badCountry";

        webTestClient.get()
                .uri("search/?country=" + country + "&page=0&size=10")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(0);

    }

    @Test
    void findByCityAndCountryOneExists() {

        String city = "gRa"; //Belgrade
        String country = "biA"; //Serbia

        webTestClient.get()
                .uri("search/?country=" + country + "&city=" + city + "&page=0&size=10")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1)
                .jsonPath("$.content[0].address.city").isEqualTo("Belgrade")
                .jsonPath("$.content[0].address.country").isEqualTo("Serbia");
    }

    @Test
    void findByCityAndCountryNoneExists() {

        String city = "badCity";
        String country = "badCountry";

        webTestClient.get()
                .uri("search/?country=" + country + "&city=" + city + "&page=0&size=10")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(0);
    }

}