package rs.ac.bg.fon.accommodationservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.accommodationservice.domain.AmenityDomain;
import rs.ac.bg.fon.accommodationservice.domain.PriceDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.PriceDomainUpdate;
import rs.ac.bg.fon.accommodationservice.dto.PriceDto;
import rs.ac.bg.fon.accommodationservice.dto.update.PriceDtoUpdate;
import rs.ac.bg.fon.accommodationservice.model.Accommodation;
import rs.ac.bg.fon.accommodationservice.model.AccommodationUnit;
import rs.ac.bg.fon.accommodationservice.model.Amenity;
import rs.ac.bg.fon.accommodationservice.model.Price;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceMapper {

    PriceDto domainToDto(PriceDomain priceDomain);

    PriceDomain dtoToDomain(PriceDto priceDto);

    PriceDomainUpdate dtoUpdateToDomainUpdate(PriceDtoUpdate priceDomainUpdate);

    @Mapping(target = "accommodationUnit", source = "accommodationUnit", qualifiedByName = "accommodationUnitToAccommodationUnitDomain")
    PriceDomain entityToDomain(Price price);

    @Mapping(target = "accommodationUnit", source = "accommodationUnit", qualifiedByName = "accommodationUnitDomainToAccommodationUnit")
    Price domainToEntity(PriceDomain priceDomain);

    @Named("accommodationUnitToAccommodationUnitDomain")
    static AccommodationUnitDomain map(AccommodationUnit accommodationUnit) {
        if (accommodationUnit == null) {
            return null;
        }

        AccommodationUnitDomain.AccommodationUnitDomainBuilder accommodationUnitDomain = AccommodationUnitDomain.builder();

        accommodationUnitDomain.id(accommodationUnit.getId());
        accommodationUnitDomain.name(accommodationUnit.getName());
        accommodationUnitDomain.description(accommodationUnit.getDescription());
        accommodationUnitDomain.capacity(accommodationUnit.getCapacity());
        accommodationUnitDomain.accommodation(accommodationToAccommodationDomain(accommodationUnit.getAccommodation()));
        accommodationUnitDomain.deleted(accommodationUnit.isDeleted());
        List<Price> prices = accommodationUnit.getPrices();
        if (prices != null) {
            accommodationUnitDomain.prices(new ArrayList<Price>(prices));
        }

        return accommodationUnitDomain.build();
    }

    @Named("accommodationUnitDomainToAccommodationUnit")
    static AccommodationUnit map(AccommodationUnitDomain accommodationUnitDomain) {
        if (accommodationUnitDomain == null) {
            return null;
        }

        AccommodationUnit.AccommodationUnitBuilder accommodationUnit = AccommodationUnit.builder();

        accommodationUnit.id(accommodationUnitDomain.getId());
        accommodationUnit.name(accommodationUnitDomain.getName());
        accommodationUnit.description(accommodationUnitDomain.getDescription());
        accommodationUnit.capacity(accommodationUnitDomain.getCapacity());
        accommodationUnit.accommodation(accommodationDomainToAccommodation(accommodationUnitDomain.getAccommodation()));
        accommodationUnit.deleted(accommodationUnitDomain.isDeleted());
        List<Price> prices = accommodationUnitDomain.getPrices();
        if (prices != null) {
            accommodationUnit.prices(new ArrayList<Price>(prices));
        }

        return accommodationUnit.build();
    }

    static AccommodationDomain accommodationToAccommodationDomain(Accommodation accommodation) {
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

    static Accommodation accommodationDomainToAccommodation(AccommodationDomain accommodationDomain) {
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
