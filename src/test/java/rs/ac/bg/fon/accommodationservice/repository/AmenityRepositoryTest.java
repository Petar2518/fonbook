package rs.ac.bg.fon.accommodationservice.repository;

import rs.ac.bg.fon.accommodationservice.model.Amenity;
import rs.ac.bg.fon.accommodationservice.util.DataJpaTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@Tag("datajpa")
class AmenityRepositoryTest extends DataJpaTestBase {
    @Autowired
    AmenityRepository amenityRepository;


    @Test
    void saveAndFindById() {
        Amenity amenity = Amenity.builder()
                .amenity("pool")
                .build();
        Amenity amenitySaved = amenityRepository.save(amenity);
        Optional<Amenity> actual = amenityRepository.findById(amenitySaved.getId());

        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(amenitySaved);
    }
}