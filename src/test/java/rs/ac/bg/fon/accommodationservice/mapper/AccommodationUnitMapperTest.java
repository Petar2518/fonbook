package rs.ac.bg.fon.accommodationservice.mapper;

import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AccommodationUnitDomainUpdate;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationDto;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationUnitDto;
import rs.ac.bg.fon.accommodationservice.dto.update.AccommodationUnitDtoUpdate;
import rs.ac.bg.fon.accommodationservice.model.Accommodation;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.AccommodationUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class AccommodationUnitMapperTest {

    AccommodationUnitMapper mapper = Mappers.getMapper(AccommodationUnitMapper.class);
    AccommodationMapper accommodationMapper = Mappers.getMapper(AccommodationMapper.class);

    @Test
    void fromEntityToDomain() {
        Accommodation accommodation = Accommodation.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AccommodationUnit accommodationUnit = AccommodationUnit.builder()
                .name("King size bedroom")
                .capacity(2)
                .description("Large room")
                .accommodation(accommodation)
                .build();
        AccommodationUnitDomain accommodationUnitDomain = mapper.entityToDomain(accommodationUnit);

        assertNotNull(accommodationUnitDomain);
        assertEquals(accommodationUnit.getCapacity(), accommodationUnitDomain.getCapacity());
        assertEquals(accommodationUnit.getName(), accommodationUnitDomain.getName());
        assertEquals(accommodationUnit.getDescription(), accommodationUnitDomain.getDescription());
        assertEquals(accommodationMapper.entityToDomain(accommodationUnit.getAccommodation()).getId(), accommodationUnitDomain.getAccommodation().getId());
        assertEquals(accommodationUnit.getId(), accommodationUnitDomain.getId());
    }

    @Test
    void fromDomainToEntity() {
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AccommodationUnitDomain accommodationUnitDomain = AccommodationUnitDomain.builder()
                .name("King size bedroom")
                .capacity(2)
                .description("Large room")
                .accommodation(accommodationDomain)
                .build();
        AccommodationUnit accommodationUnit = mapper.domainToEntity(accommodationUnitDomain);

        assertNotNull(accommodationUnit);
        assertEquals(accommodationUnit.getCapacity(), accommodationUnitDomain.getCapacity());
        assertEquals(accommodationUnit.getName(), accommodationUnitDomain.getName());
        assertEquals(accommodationUnit.getDescription(), accommodationUnitDomain.getDescription());
        assertEquals(accommodationUnit.getAccommodation().getId(), accommodationMapper.domainToEntity(accommodationUnitDomain.getAccommodation()).getId());
        assertEquals(accommodationUnit.getId(), accommodationUnitDomain.getId());
    }

    @Test
    void fromDomainToDto() {
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AccommodationUnitDomain accommodationUnitDomain = AccommodationUnitDomain.builder()
                .name("King size bedroom")
                .capacity(2)
                .description("Large room")
                .accommodation(accommodationDomain)
                .build();
        AccommodationUnitDto accommodationUnitDto = mapper.domainToDto(accommodationUnitDomain);

        assertNotNull(accommodationUnitDto);
        assertEquals(accommodationUnitDto.getCapacity(), accommodationUnitDomain.getCapacity());
        assertEquals(accommodationUnitDto.getName(), accommodationUnitDomain.getName());
        assertEquals(accommodationUnitDto.getDescription(), accommodationUnitDomain.getDescription());
        assertEquals(accommodationUnitDto.getAccommodation().getId(), accommodationMapper.domainToDto(accommodationUnitDomain.getAccommodation()).getId());
        assertEquals(accommodationUnitDto.getId(), accommodationUnitDomain.getId());
    }

    @Test
    void fromDtoToDomain() {
        AccommodationDto accommodationDto = AccommodationDto.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AccommodationUnitDto accommodationUnitDto = AccommodationUnitDto.builder()
                .name("King size bedroom")
                .capacity(2)
                .description("Large room")
                .accommodation(accommodationDto)
                .build();
        AccommodationUnitDomain accommodationUnitDomain = mapper.dtoToDomain(accommodationUnitDto);


        assertNotNull(accommodationUnitDomain);
        assertEquals(accommodationUnitDomain.getCapacity(), accommodationUnitDto.getCapacity());
        assertEquals(accommodationUnitDomain.getName(), accommodationUnitDto.getName());
        assertEquals(accommodationUnitDomain.getDescription(), accommodationUnitDto.getDescription());
        assertEquals(accommodationUnitDomain.getAccommodation().getId(), accommodationMapper.dtoToDomain(accommodationUnitDto.getAccommodation()).getId());
        assertEquals(accommodationUnitDomain.getId(), accommodationUnitDto.getId());
    }

    @Test
    void fromDtoUpdatedToDomainUpdated() {
        AccommodationUnitDtoUpdate accommodationUnitDto = AccommodationUnitDtoUpdate.builder()
                .name("King size bedroom")
                .capacity(2)
                .description("Large room")
                .build();
        AccommodationUnitDomainUpdate accommodationUnitDomain = mapper.dtoUpdateToDomainUpdate(accommodationUnitDto);


        assertNotNull(accommodationUnitDomain);
        assertEquals(accommodationUnitDomain.getCapacity(), accommodationUnitDto.getCapacity());
        assertEquals(accommodationUnitDomain.getName(), accommodationUnitDto.getName());
        assertEquals(accommodationUnitDomain.getDescription(), accommodationUnitDto.getDescription());
        assertEquals(accommodationUnitDomain.getId(), accommodationUnitDto.getId());
    }
}
