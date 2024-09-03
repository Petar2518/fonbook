package rs.ac.bg.fon.searchservice.mapper;

import rs.ac.bg.fon.searchservice.domain.AmenityDomain;
import rs.ac.bg.fon.searchservice.dto.message.AmenityMessageDto;
import rs.ac.bg.fon.searchservice.model.Amenity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("springboot")
public class AmenityMapperTest {

    @Autowired
    AmenityMapper amenityMapper;

    @Test
    void domainToEntity() {
        AmenityDomain amenityDomain = AmenityDomain.builder()
                .id(1L)
                .amenity("amenity").build();

        Amenity expected = Amenity.builder()
                .id(1L)
                .amenity("amenity").build();

        Amenity amenity = amenityMapper.domainToEntity(amenityDomain);

        assertThat(amenity).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void messageDtoToDomain() {
        AmenityMessageDto amenityMessageDto = AmenityMessageDto.builder()
                .id(1L)
                .amenity("amenity")
                .build();

        AmenityDomain expected = AmenityDomain.builder()
                .id(1L)
                .amenity("amenity")
                .build();

        AmenityDomain amenityDomain = amenityMapper.messageDtoToDomain(amenityMessageDto);

        assertThat(amenityDomain).usingRecursiveComparison().isEqualTo(expected);
    }
}
