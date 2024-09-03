package rs.ac.bg.fon.accommodationservice.mapper;

import rs.ac.bg.fon.accommodationservice.domain.AmenityDomain;
import rs.ac.bg.fon.accommodationservice.dto.AmenityDto;
import rs.ac.bg.fon.accommodationservice.model.Amenity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AmenitiesMapperTest {
    AmenityMapper mapper = Mappers.getMapper(AmenityMapper.class);

    @Test
    void fromEntityToDomain() {
        Amenity amenity = Amenity.builder()
                .amenity("pool")
                .build();
        AmenityDomain amenityDomain = mapper.entityToDomain(amenity);

        assertNotNull(amenityDomain);
        assertEquals(amenityDomain.getAmenity(), amenity.getAmenity());
    }

    @Test
    void fromDomainToEntity() {

        AmenityDomain amenityDomain = AmenityDomain.builder()
                .amenity("pool")
                .build();
        Amenity amenity = mapper.domainToEntity(amenityDomain);

        assertNotNull(amenity);
        assertEquals(amenityDomain.getAmenity(), amenity.getAmenity());
    }

    @Test
    void fromDomainToDto() {
        AmenityDomain amenityDomain = AmenityDomain.builder()
                .amenity("pool")
                .build();
        AmenityDto amenityDto = mapper.domainToDto(amenityDomain);

        assertNotNull(amenityDto);
        assertEquals(amenityDomain.getAmenity(), amenityDto.getAmenity());
    }

    @Test
    void fromDtoToDomain() {
        AmenityDto amenityDto = AmenityDto.builder()
                .amenity("pool")
                .build();
        AmenityDomain amenityDomain = mapper.dtoToDomain(amenityDto);

        assertNotNull(amenityDomain);
        assertEquals(amenityDomain.getAmenity(), amenityDto.getAmenity());

    }
}
