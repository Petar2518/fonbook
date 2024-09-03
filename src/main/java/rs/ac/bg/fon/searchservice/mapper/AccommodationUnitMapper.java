package rs.ac.bg.fon.searchservice.mapper;

import rs.ac.bg.fon.searchservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.searchservice.dto.message.AccommodationUnitMessageDto;
import rs.ac.bg.fon.searchservice.model.AccommodationUnit;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccommodationUnitMapper {

    AccommodationUnit domainToEntity(AccommodationUnitDomain accommodationUnitDomain);

    AccommodationUnitDomain entityToDomain(AccommodationUnit accommodationUnit);

    List<AccommodationUnitDomain> entitiesToDomains(List<AccommodationUnit> accommodationUnits);

    AccommodationUnitDomain messageDtoToDomain(AccommodationUnitMessageDto unit);
}