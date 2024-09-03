package rs.ac.bg.fon.accommodationservice.mapper;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.accommodationservice.domain.AmenityDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AccommodationUnitDomainUpdate;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationUnitDto;
import rs.ac.bg.fon.accommodationservice.dto.update.AccommodationUnitDtoUpdate;
import rs.ac.bg.fon.accommodationservice.model.Accommodation;
import rs.ac.bg.fon.accommodationservice.model.AccommodationUnit;
import rs.ac.bg.fon.accommodationservice.model.Amenity;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AccommodationUnitMapper {

    AccommodationUnitDto domainToDto(AccommodationUnitDomain accommodationUnitDomain);

    AccommodationUnitDomainUpdate dtoUpdateToDomainUpdate(AccommodationUnitDtoUpdate accommodationDtoUpdate);

    AccommodationUnitDomain dtoToDomain(AccommodationUnitDto accommodationUnitDto);

    @Mapping(target = "accommodation", source = "accommodation", qualifiedByName = "accommodationDomainToAccommodation")
    AccommodationUnit domainToEntity(AccommodationUnitDomain accommodationUnitDomain);

    @Mapping(target = "accommodation", source = "accommodation", qualifiedByName = "accommodationToAccommodationDomain")
    AccommodationUnitDomain entityToDomain(AccommodationUnit accommodationUnit);

    @Named("accommodationDomainToAccommodation")
    static Accommodation customMapDomainToEntity(AccommodationDomain accommodationDomain) {
        if (accommodationDomain == null) {
            return null;
        }

        Accommodation.AccommodationBuilder accommodation = Accommodation.builder();

        accommodation.id(accommodationDomain.getId());
        accommodation.name(accommodationDomain.getName());
        accommodation.description(accommodationDomain.getDescription());
        accommodation.accommodationType(accommodationDomain.getAccommodationType());
        accommodation.hostId(accommodationDomain.getHostId());
        accommodation.amenities(amenityDomainListToAmenityList(accommodationDomain.getAmenities()));
        accommodation.deleted(accommodationDomain.isDeleted());

        return accommodation.build();
    }

    @Named("accommodationToAccommodationDomain")
    static AccommodationDomain customMapEntityToDomain(Accommodation accommodation) {
        if (accommodation == null) {
            return null;
        }

        AccommodationDomain.AccommodationDomainBuilder accommodationDomain = AccommodationDomain.builder();

        accommodationDomain.id(accommodation.getId());
        accommodationDomain.name(accommodation.getName());
        accommodationDomain.description(accommodation.getDescription());
        accommodationDomain.accommodationType(accommodation.getAccommodationType());
        accommodationDomain.hostId(accommodation.getHostId());
        accommodationDomain.amenities(amenityListToAmenityDomainList(accommodation.getAmenities()));
        accommodationDomain.deleted(accommodation.isDeleted());
        return accommodationDomain.build();
    }


    static AmenityDomain amenityToAmenityDomain(Amenity amenity) {
        if (amenity == null) {
            return null;
        }

        AmenityDomain.AmenityDomainBuilder amenityDomain = AmenityDomain.builder();

        amenityDomain.id(amenity.getId());
        amenityDomain.amenity(amenity.getAmenity());

        return amenityDomain.build();
    }

    static List<AmenityDomain> amenityListToAmenityDomainList(List<Amenity> amenities) {
        if (amenities == null) {
            return null;
        }

        List<AmenityDomain> amenityDomains = new ArrayList<AmenityDomain>(amenities.size());
        for (Amenity amenity : amenities) {
            amenityDomains.add(amenityToAmenityDomain(amenity));
        }

        return amenityDomains;
    }

    static List<Amenity> amenityDomainListToAmenityList(List<AmenityDomain> amenityDomains) {
        if (amenityDomains == null) {
            return null;
        }

        List<Amenity> amenities = new ArrayList<Amenity>(amenityDomains.size());
        for (AmenityDomain amenityDomain : amenityDomains) {
            amenities.add(amenityDomainToAmenity(amenityDomain));
        }

        return amenities;
    }

    static Amenity amenityDomainToAmenity(AmenityDomain amenityDomain) {
        if (amenityDomain == null) {
            return null;
        }

        Amenity.AmenityBuilder amenity = Amenity.builder();

        amenity.id(amenityDomain.getId());
        amenity.amenity(amenityDomain.getAmenity());

        return amenity.build();
    }
}
