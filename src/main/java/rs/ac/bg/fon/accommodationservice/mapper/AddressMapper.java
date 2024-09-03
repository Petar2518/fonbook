package rs.ac.bg.fon.accommodationservice.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.accommodationservice.domain.AddressDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AddressDomainUpdate;
import rs.ac.bg.fon.accommodationservice.dto.AddressDto;
import rs.ac.bg.fon.accommodationservice.dto.update.AddressDtoUpdate;
import rs.ac.bg.fon.accommodationservice.model.AccommodationUnit;
import rs.ac.bg.fon.accommodationservice.model.Address;

@Mapper(componentModel = "spring")

public interface AddressMapper {
    AddressDto domainToDto(AddressDomain addressDomain);

    AddressDomainUpdate dtoUpdateToDomainUpdate(AddressDtoUpdate addressDtoUpdate);

    AddressDomain dtoToDomain(AddressDto addressDto);

    @Mapping(target="accommodation", ignore = true)
    AddressDomain entityToDomain(Address address);

    Address domainToEntity(AddressDomain addressDomain);

}
