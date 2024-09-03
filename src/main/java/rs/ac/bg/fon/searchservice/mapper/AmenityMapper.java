package rs.ac.bg.fon.searchservice.mapper;

import rs.ac.bg.fon.searchservice.domain.AmenityDomain;
import rs.ac.bg.fon.searchservice.dto.message.AmenityMessageDto;
import rs.ac.bg.fon.searchservice.model.Amenity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AmenityMapper {

    Amenity domainToEntity(AmenityDomain amenityDomain);

    AmenityDomain messageDtoToDomain(AmenityMessageDto amenityMessageDto);
}
