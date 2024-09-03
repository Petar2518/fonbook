package rs.ac.bg.fon.searchservice.service;

import rs.ac.bg.fon.searchservice.domain.AccommodationDomain;
import rs.ac.bg.fon.searchservice.domain.AddressDomain;
import rs.ac.bg.fon.searchservice.mapper.AccommodationMapper;
import rs.ac.bg.fon.searchservice.mapper.AddressMapper;
import rs.ac.bg.fon.searchservice.model.Accommodation;
import rs.ac.bg.fon.searchservice.model.AccommodationType;
import rs.ac.bg.fon.searchservice.model.Address;
import rs.ac.bg.fon.searchservice.repository.AccommodationRepository;
import rs.ac.bg.fon.searchservice.service.impl.AccommodationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceTest {

    @Mock
    AccommodationRepository accommodationRepository;
    @Mock
    AccommodationMapper accommodationMapper;
    @Mock
    AddressMapper addressMapper;
    @InjectMocks
    AccommodationServiceImpl accommodationService;

    @Test
    public void saveAccommodation() {
        Address address = Address.builder()
                .id(1L)
                .country("Serbia")
                .city("Krusevac")
                .street("ulica")
                .streetNumber("12a")
                .postalCode("37000").build();
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .id(1L)
                .name("acc name")
                .accommodationType(AccommodationType.HOTEL)
                .address(address)
                .build();
        Accommodation accommodation = Accommodation.builder()
                .id(1L)
                .name("acc name")
                .accommodationType(AccommodationType.HOTEL)
                .address(address)
                .build();

        when(accommodationMapper.domainToEntity(accommodationDomain)).thenReturn(accommodation);

        accommodationService.save(accommodationDomain);

        verify(accommodationRepository).save(accommodation);
    }

    @Test
    void addAddress() {
        AddressDomain addressDomain = AddressDomain.builder()
                .id(1L)
                .city("city")
                .country("country")
                .postalCode("37000")
                .street("street")
                .build();
        Address address = Address.builder()
                .id(1L)
                .city("city")
                .country("country")
                .postalCode("37000")
                .street("street")
                .build();
        Accommodation accommodation = Accommodation.builder()
                .id(1L)
                .name("acc name")
                .accommodationType(AccommodationType.HOTEL)
                .build();
        Accommodation accommodationToSave = Accommodation.builder()
                .id(1L)
                .name("acc name")
                .accommodationType(AccommodationType.HOTEL)
                .address(address)
                .build();

        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(addressMapper.domainToEntity(addressDomain)).thenReturn(address);

        accommodationService.addAddress(addressDomain, 1L);

        verify(accommodationRepository).save(accommodationToSave);
    }
}
