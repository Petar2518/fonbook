package rs.ac.bg.fon.accommodationservice.mapper;

import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.AddressDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AddressDomainUpdate;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationDto;
import rs.ac.bg.fon.accommodationservice.dto.AddressDto;
import rs.ac.bg.fon.accommodationservice.dto.update.AddressDtoUpdate;
import rs.ac.bg.fon.accommodationservice.model.Accommodation;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class AddressMapperTest {

    AddressMapper mapper = Mappers.getMapper(AddressMapper.class);
    AccommodationMapper accommodationMapper = Mappers.getMapper(AccommodationMapper.class);

    @Test
    void fromEntityToDomain() {
        Accommodation accommodation = Accommodation.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        Address address = Address.builder()
                .accommodation(accommodation)
                .country("Serbia")
                .city("Belgrade")
                .postalCode("11000")
                .street("Bulevar Kralja Aleksandra")
                .streetNumber("113a")
                .build();
        AddressDomain addressDomain = mapper.entityToDomain(address);

        assertNotNull(addressDomain);
        assertEquals(address.getCountry(), addressDomain.getCountry());
        assertEquals(address.getCity(), addressDomain.getCity());
        assertEquals(address.getPostalCode(), addressDomain.getPostalCode());
        assertEquals(address.getStreet(), addressDomain.getStreet());
        assertEquals(address.getStreetNumber(), addressDomain.getStreetNumber());
        assertEquals(address.getId(), addressDomain.getId());
    }

    @Test
    void fromDomainToEntity() {
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AddressDomain addressDomain = AddressDomain.builder()
                .accommodation(accommodationDomain)
                .country("Serbia")
                .city("Belgrade")
                .postalCode("11000")
                .street("Bulevar Kralja Aleksandra")
                .streetNumber("113a")
                .build();
        Address address = mapper.domainToEntity(addressDomain);

        assertNotNull(address);
        assertEquals(accommodationMapper.entityToDomain(address.getAccommodation()).getId(), addressDomain.getAccommodation().getId());
        assertEquals(address.getCountry(), addressDomain.getCountry());
        assertEquals(address.getCity(), addressDomain.getCity());
        assertEquals(address.getPostalCode(), addressDomain.getPostalCode());
        assertEquals(address.getStreet(), addressDomain.getStreet());
        assertEquals(address.getStreetNumber(), addressDomain.getStreetNumber());
        assertEquals(address.getId(), addressDomain.getId());
    }

    @Test
    void fromDomainToDto() {
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AddressDomain addressDomain = AddressDomain.builder()
                .accommodation(accommodationDomain)
                .country("Serbia")
                .city("Belgrade")
                .postalCode("11000")
                .street("Bulevar Kralja Aleksandra")
                .streetNumber("113a")
                .build();
        AddressDto addressDto = mapper.domainToDto(addressDomain);

        assertNotNull(addressDto);
        assertEquals(accommodationMapper.dtoToDomain(addressDto.getAccommodation()).getId(), addressDomain.getAccommodation().getId());
        assertEquals(addressDto.getCountry(), addressDomain.getCountry());
        assertEquals(addressDto.getCity(), addressDomain.getCity());
        assertEquals(addressDto.getPostalCode(), addressDomain.getPostalCode());
        assertEquals(addressDto.getStreet(), addressDomain.getStreet());
        assertEquals(addressDto.getStreetNumber(), addressDomain.getStreetNumber());
        assertEquals(addressDto.getId(), addressDomain.getId());
    }

    @Test
    void fromDtoToDomain() {
        AccommodationDto accommodationDto = AccommodationDto.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AddressDto addressDto = AddressDto.builder()
                .accommodation(accommodationDto)
                .country("Serbia")
                .city("Belgrade")
                .postalCode("11000")
                .street("Bulevar Kralja Aleksandra")
                .streetNumber("113a")
                .build();
        AddressDomain addressDomain = mapper.dtoToDomain(addressDto);


        assertNotNull(addressDomain);
        assertNotNull(addressDto);
        assertEquals(accommodationMapper.dtoToDomain(addressDto.getAccommodation()).getId(), addressDomain.getAccommodation().getId());
        assertEquals(addressDto.getCountry(), addressDomain.getCountry());
        assertEquals(addressDto.getCity(), addressDomain.getCity());
        assertEquals(addressDto.getPostalCode(), addressDomain.getPostalCode());
        assertEquals(addressDto.getStreet(), addressDomain.getStreet());
        assertEquals(addressDto.getStreetNumber(), addressDomain.getStreetNumber());
        assertEquals(addressDto.getId(), addressDomain.getId());
    }

    @Test
    void fromDtoUpdateToDomainUpdate() {

        AddressDtoUpdate addressDto = AddressDtoUpdate.builder()
                .postalCode("11000")
                .street("Bulevar Kralja Aleksandra")
                .streetNumber("113a")
                .build();
        AddressDomainUpdate addressDomain = mapper.dtoUpdateToDomainUpdate(addressDto);


        assertNotNull(addressDomain);
        assertNotNull(addressDto);

        assertEquals(addressDto.getPostalCode(), addressDomain.getPostalCode());
        assertEquals(addressDto.getStreet(), addressDomain.getStreet());
        assertEquals(addressDto.getStreetNumber(), addressDomain.getStreetNumber());
        assertEquals(addressDto.getId(), addressDomain.getId());
    }
}
