package rs.ac.bg.fon.accommodationservice.mapper;

import org.mapstruct.Mapper;
import rs.ac.bg.fon.accommodationservice.domain.AmenityDomain;
import rs.ac.bg.fon.accommodationservice.dto.AmenityDto;
import rs.ac.bg.fon.accommodationservice.model.Amenity;

@Mapper(componentModel = "spring")
public interface AmenityMapper {
    AmenityDto domainToDto(AmenityDomain amenityDomain);

    AmenityDomain dtoToDomain(AmenityDto amenityDto);

    AmenityDomain entityToDomain(Amenity amenity);

    Amenity domainToEntity(AmenityDomain amenityDomain);
}
