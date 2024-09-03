package rs.ac.bg.fon.accommodationservice.repository;

import rs.ac.bg.fon.accommodationservice.model.Accommodation;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.Image;
import rs.ac.bg.fon.accommodationservice.util.DataJpaTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("datajpa")
class ImageRepositoryTest extends DataJpaTestBase {
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    AccommodationRepository accRepository;


    @Test
    void saveAndFindById() {
        Accommodation acc = Accommodation.builder()
                .name("Apartment 1")
                .accommodationType(AccommodationType.APARTMENT)
                .hostId(5L)
                .build();
        Accommodation accSaved = accRepository.save(acc);
        byte[] fileContent = "We are checking. . .".getBytes();

        Image image = Image.builder()
                .accommodation(accSaved)
                .image(fileContent)
                .build();
        Image imageSaved = imageRepository.save(image);
        Optional<Image> actual = imageRepository.findById(imageSaved.getId());

        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(imageSaved);
    }
}