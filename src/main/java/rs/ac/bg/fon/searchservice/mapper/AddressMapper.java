package rs.ac.bg.fon.searchservice.mapper;

import rs.ac.bg.fon.searchservice.domain.AddressDomain;
import rs.ac.bg.fon.searchservice.dto.message.AddressMessageDto;
import rs.ac.bg.fon.searchservice.model.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address domainToEntity(AddressDomain addressDomain);

    AddressDomain messageDtoToDomain(AddressMessageDto addressMessageDto);
}
