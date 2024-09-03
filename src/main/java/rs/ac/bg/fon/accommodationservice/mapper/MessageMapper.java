package rs.ac.bg.fon.accommodationservice.mapper;

import rs.ac.bg.fon.accommodationservice.dto.message.*;
import rs.ac.bg.fon.accommodationservice.model.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    AccommodationMessageDto accommodationEntityToMessageDto(Accommodation accommodation);

    Accommodation accommodationMessageDtoToEntity(AccommodationMessageDto accommodationMessageDto);

    AccommodationUnitMessageDto accommodationUnitEntityToMessageDto(AccommodationUnit accommodationUnit);

    AccommodationUnit accommodationUnitMessageDtoToEntity(AccommodationUnitMessageDto accommodationUnitMessageDto);


    AddressMessageDto addressEntityToMessageDto(Address address);

    Address addressMessageDtoToEntity(AddressMessageDto addressMessageDto);

    PriceMessageDto priceEntityToMessageDto(Price price);

    Price priceMessageDtoToEntity(PriceMessageDto priceMessageDto);


}
