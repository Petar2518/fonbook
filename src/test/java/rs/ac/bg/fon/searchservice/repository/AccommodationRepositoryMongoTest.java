package rs.ac.bg.fon.searchservice.repository;

import rs.ac.bg.fon.searchservice.model.Accommodation;
import rs.ac.bg.fon.searchservice.model.Address;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import util.MongoContainerInitializer;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("datajpa")
class AccommodationRepositoryMongoTest extends MongoContainerInitializer {

    @Autowired
    AccommodationRepository accommodationRepository;

    private static Address address;

    @Test
    void saveAddress() {

        address = Address.builder()
                .id(5)
                .country("addressCountry")
                .city("addressCity")
                .build();

    }


    @Test
    void saveAndFindById() {

        Accommodation accommodation = Accommodation.builder()
                .id(10L)
                .name("HotelName")
                .address(address)
                .accommodationUnits(new HashSet<>())
                .build();

        accommodationRepository.save(accommodation);
        Accommodation retrievedAccommodation =
                accommodationRepository.findById(accommodation.getId()).orElse(null);

        assertThat(retrievedAccommodation).isNotNull();
        assertThat(retrievedAccommodation).usingRecursiveComparison().isEqualTo(accommodation);
    }


}