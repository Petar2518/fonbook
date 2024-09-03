package rs.ac.bg.fon.accommodationservice.mapper;

import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AccommodationDomainUpdate;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationDto;
import rs.ac.bg.fon.accommodationservice.dto.update.AccommodationDtoUpdate;
import rs.ac.bg.fon.accommodationservice.model.Accommodation;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
class AccommodationMapperTest {

    AccommodationMapper mapper = Mappers.getMapper(AccommodationMapper.class);

    @Test
    void fromEntityToDomain() {
        Accommodation accommodation = Accommodation.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AccommodationDomain accommodationDomain = mapper.entityToDomain(accommodation);

        assertNotNull(accommodationDomain);
        assertEquals(accommodation.getAccommodationType(), accommodationDomain.getAccommodationType());
        assertEquals(accommodation.getName(), accommodationDomain.getName());
        assertEquals(accommodation.getDescription(), accommodationDomain.getDescription());
        assertEquals(accommodation.getHostId(), accommodationDomain.getHostId());
        assertEquals(accommodation.getId(), accommodationDomain.getId());
    }

    @Test
    void fromDomainToEntity() {
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        Accommodation accommodation = mapper.domainToEntity(accommodationDomain);

        assertNotNull(accommodationDomain);
        assertEquals(accommodation.getAccommodationType(), accommodationDomain.getAccommodationType());
        assertEquals(accommodation.getName(), accommodationDomain.getName());
        assertEquals(accommodation.getDescription(), accommodationDomain.getDescription());
        assertEquals(accommodation.getHostId(), accommodationDomain.getHostId());
        assertEquals(accommodation.getId(), accommodationDomain.getId());
    }

    @Test
    void fromDomainToDto() {
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AccommodationDto accommodationDto = mapper.domainToDto(accommodationDomain);

        assertNotNull(accommodationDomain);
        assertEquals(accommodationDto.getAccommodationType(), accommodationDomain.getAccommodationType());
        assertEquals(accommodationDto.getName(), accommodationDomain.getName());
        assertEquals(accommodationDto.getDescription(), accommodationDomain.getDescription());
        assertEquals(accommodationDto.getHostId(), accommodationDomain.getHostId());
        assertEquals(accommodationDto.getId(), accommodationDomain.getId());
    }

    @Test
    void fromDtoToDomain() {
        AccommodationDto accommodationDto = AccommodationDto.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AccommodationDomain accommodationDomain = mapper.dtoToDomain(accommodationDto);

        assertNotNull(accommodationDomain);
        assertEquals(accommodationDto.getAccommodationType(), accommodationDomain.getAccommodationType());
        assertEquals(accommodationDto.getName(), accommodationDomain.getName());
        assertEquals(accommodationDto.getDescription(), accommodationDomain.getDescription());
        assertEquals(accommodationDto.getHostId(), accommodationDomain.getHostId());
        assertEquals(accommodationDto.getId(), accommodationDomain.getId());
    }

    @Test
    void fromDtoUpdatedToDomainUpdated() {
        AccommodationDtoUpdate accommodationDto = AccommodationDtoUpdate.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .description("Nice hotel")
                .build();
        AccommodationDomainUpdate accommodationDomain = mapper.dtoUpdateToDomainUpdate(accommodationDto);

        assertNotNull(accommodationDomain);
        assertEquals(accommodationDto.getAccommodationType(), accommodationDomain.getAccommodationType());
        assertEquals(accommodationDto.getName(), accommodationDomain.getName());
        assertEquals(accommodationDto.getDescription(), accommodationDomain.getDescription());
        assertEquals(accommodationDto.getId(), accommodationDomain.getId());
    }
}