package rs.ac.bg.fon.searchservice.mapper;

import rs.ac.bg.fon.searchservice.domain.AccommodationDomain;
import rs.ac.bg.fon.searchservice.dto.AccommodationDto;
import rs.ac.bg.fon.searchservice.dto.message.AccommodationMessageDto;
import rs.ac.bg.fon.searchservice.model.Accommodation;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.HashSet;

@Mapper(componentModel = "spring")
public interface AccommodationMapper {

    Accommodation domainToEntity(AccommodationDomain accommodationDomain);

    AccommodationDomain entityToDomain(Accommodation accommodation);

    default Page<AccommodationDomain> entitiesToDomains(Page<Accommodation> accommodations) {
        return accommodations.map(this::entityToDomain);
    }

    AccommodationDto domainToDto(AccommodationDomain accommodationDomain);

    default Page<AccommodationDto> domainsToDtos(Page<AccommodationDomain> accommodationDomains) {
        return accommodationDomains.map(this::domainToDto);
    }

    AccommodationDomain messageDtoToDomainWithoutUnits(AccommodationMessageDto accommodationMessageDto);

    default AccommodationDomain messageDtoToDomain(AccommodationMessageDto accommodationMessageDto) {
        AccommodationDomain accommodationDomain = messageDtoToDomainWithoutUnits(accommodationMessageDto);
        accommodationDomain.setAccommodationUnits(new HashSet<>());
        return accommodationDomain;
    }

}
