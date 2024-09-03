package rs.ac.bg.fon.accommodationservice.mapper;

import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.ImageDomain;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationDto;
import rs.ac.bg.fon.accommodationservice.dto.ImageDto;
import rs.ac.bg.fon.accommodationservice.model.Accommodation;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.Image;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class ImageMapperTest {
    ImageMapper mapper = Mappers.getMapper(ImageMapper.class);
    AccommodationMapper accommodationMapper = Mappers.getMapper(AccommodationMapper.class);

    @Test
    void fromEntityToDomain() {
        Accommodation accommodation = Accommodation.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        Image image = Image.builder()
                .image("We are checking. . .".getBytes())
                .accommodation(accommodation)
                .build();

        ImageDomain imageDomain = mapper.entityToDomain(image);

        assertNotNull(imageDomain);
        assertEquals(accommodationMapper.entityToDomain(image.getAccommodation()).getId(), imageDomain.getAccommodation().getId());
        assertThat(imageDomain).extracting(ImageDomain::getImage).isEqualTo(image.getImage());

    }

    @Test
    void fromDomainToEntity() {
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        ImageDomain imageDomain = ImageDomain.builder()
                .image("We are checking. . .".getBytes())
                .accommodation(accommodationDomain)
                .build();
        Image image = mapper.domainToEntity(imageDomain);

        assertNotNull(image);
        assertEquals(accommodationMapper.entityToDomain(image.getAccommodation()).getId(), imageDomain.getAccommodation().getId());
        assertThat(imageDomain).extracting(ImageDomain::getImage).isEqualTo(image.getImage());

    }

    @Test
    void fromDomainToDto() {
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        ImageDomain imageDomain = ImageDomain.builder()
                .image("We are checking. . .".getBytes())
                .accommodation(accommodationDomain)
                .build();
        ImageDto imageDto = mapper.domainToDto(imageDomain);

        assertNotNull(imageDto);
        assertEquals(accommodationMapper.dtoToDomain(imageDto.getAccommodation()).getId(), imageDomain.getAccommodation().getId());
        assertThat(imageDomain).extracting(ImageDomain::getImage).isEqualTo(imageDto.getImage());
    }

    @Test
    void fromDtoToDomain() {
        AccommodationDto accommodationDto = AccommodationDto.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        ImageDto imageDto = ImageDto.builder()
                .image("We are checking. . .".getBytes())
                .accommodation(accommodationDto)
                .build();
        ImageDomain imageDomain = mapper.dtoToDomain(imageDto);


        assertNotNull(imageDomain);
        assertEquals(accommodationMapper.dtoToDomain(imageDto.getAccommodation()).getId(), imageDomain.getAccommodation().getId());

        assertThat(imageDomain).extracting(ImageDomain::getImage).isEqualTo(imageDto.getImage());
    }
}

