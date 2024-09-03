package rs.ac.bg.fon.accommodationservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AccommodationDomainUpdate;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationDto;
import rs.ac.bg.fon.accommodationservice.dto.create.AccommodationDtoCreate;
import rs.ac.bg.fon.accommodationservice.dto.update.AccommodationDtoUpdate;
import rs.ac.bg.fon.accommodationservice.model.Accommodation;
import rs.ac.bg.fon.accommodationservice.model.AccommodationUnit;
import rs.ac.bg.fon.accommodationservice.model.Price;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AccommodationMapper {
    AccommodationDto domainToDto(AccommodationDomain accommodationDomain);

    AccommodationDomain dtoCreateToDomain(AccommodationDtoCreate accommodationDtoCreate, Long hostId);

    AccommodationDomainUpdate dtoUpdateToDomainUpdate(AccommodationDtoUpdate accommodationUpdate);

    AccommodationDomain dtoToDomain(AccommodationDto accommodationDto);

    @Mapping(target = "units", source = "units", qualifiedByName = "accommodationUnitToAccommodationUnitDomain")
    AccommodationDomain entityToDomain(Accommodation accommodation);

    @Mapping(target = "units", source = "units", qualifiedByName = "accommodationUnitDomainToAccommodationUnit")
    Accommodation domainToEntity(AccommodationDomain accommodationDomain);

    @Named("accommodationUnitDomainToAccommodationUnit")
    static List<AccommodationUnit> customMapDomainsToEntities(List<AccommodationUnitDomain> accommodationUnitDomains) {
        if (accommodationUnitDomains == null) {
            return null;
        }

        List<AccommodationUnit> accommodationUnits = new ArrayList<AccommodationUnit>(accommodationUnitDomains.size());
        for (AccommodationUnitDomain accommodationUnitDomain : accommodationUnitDomains) {
            accommodationUnits.add(accommodationUnitDomainToAccommodationUnit(accommodationUnitDomain));
        }

        return accommodationUnits;
    }

    @Named("accommodationUnitToAccommodationUnitDomain")
    static List<AccommodationUnitDomain> customMapEntitiesToDomains(List<AccommodationUnit> accommodationUnits) {
        if (accommodationUnits == null) {
            return null;
        }

        List<AccommodationUnitDomain> accommodationUnitDomains = new ArrayList<AccommodationUnitDomain>(accommodationUnits.size());
        for (AccommodationUnit accommodationUnit : accommodationUnits) {
            accommodationUnitDomains.add(accommodationUnitToAccommodationUnitDomain(accommodationUnit));
        }

        return accommodationUnitDomains;
    }

    static AccommodationUnit accommodationUnitDomainToAccommodationUnit(AccommodationUnitDomain accommodationUnitDomain) {
        if (accommodationUnitDomain == null) {
            return null;
        }

        AccommodationUnit.AccommodationUnitBuilder accommodationUnit = AccommodationUnit.builder();

        accommodationUnit.id(accommodationUnitDomain.getId());
        accommodationUnit.name(accommodationUnitDomain.getName());
        accommodationUnit.description(accommodationUnitDomain.getDescription());
        accommodationUnit.capacity(accommodationUnitDomain.getCapacity());
        accommodationUnit.accommodation(customDomainToEntity(accommodationUnitDomain.getAccommodation()));
        accommodationUnit.deleted(accommodationUnitDomain.isDeleted());
        List<Price> prices = accommodationUnitDomain.getPrices();
        if (prices != null) {
            accommodationUnit.prices(new ArrayList<Price>(prices));
        }

        return accommodationUnit.build();
    }

    static Accommodation customDomainToEntity(AccommodationDomain accommodationDomain) {
        if (accommodationDomain == null) {
            return null;
        }

        Accommodation.AccommodationBuilder accommodation = Accommodation.builder();

        accommodation.id(accommodationDomain.getId());
        accommodation.name(accommodationDomain.getName());
        accommodation.description(accommodationDomain.getDescription());
        accommodation.accommodationType(accommodationDomain.getAccommodationType());
        accommodation.hostId(accommodationDomain.getHostId());
        accommodation.deleted(accommodationDomain.isDeleted());
        return accommodation.build();
    }

    static AccommodationDomain customEntityToDomain(Accommodation accommodation) {
        if (accommodation == null) {
            return null;
        }

        AccommodationDomain.AccommodationDomainBuilder accommodationDomain = AccommodationDomain.builder();

        accommodationDomain.id(accommodation.getId());
        accommodationDomain.name(accommodation.getName());
        accommodationDomain.description(accommodation.getDescription());
        accommodationDomain.accommodationType(accommodation.getAccommodationType());
        accommodationDomain.hostId(accommodation.getHostId());
        accommodationDomain.deleted(accommodation.isDeleted());

        return accommodationDomain.build();
    }

    static AccommodationUnitDomain accommodationUnitToAccommodationUnitDomain(AccommodationUnit accommodationUnit) {
        if (accommodationUnit == null) {
            return null;
        }

        AccommodationUnitDomain.AccommodationUnitDomainBuilder accommodationUnitDomain = AccommodationUnitDomain.builder();

        accommodationUnitDomain.id(accommodationUnit.getId());
        accommodationUnitDomain.name(accommodationUnit.getName());
        accommodationUnitDomain.description(accommodationUnit.getDescription());
        accommodationUnitDomain.capacity(accommodationUnit.getCapacity());
        accommodationUnitDomain.accommodation(customEntityToDomain(accommodationUnit.getAccommodation()));
        accommodationUnitDomain.deleted(accommodationUnit.isDeleted());
        List<Price> prices = accommodationUnit.getPrices();
        if (prices != null) {
            accommodationUnitDomain.prices(new ArrayList<Price>(prices));
        }

        return accommodationUnitDomain.build();
    }
}
