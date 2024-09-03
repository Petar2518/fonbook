package rs.ac.bg.fon.accommodationservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.AccommodationDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.AddressDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.AddressDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AddressDomainUpdate;
import rs.ac.bg.fon.accommodationservice.exception.specific.AddressNotFoundException;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.JwtReceiver.Role;
import rs.ac.bg.fon.accommodationservice.model.JwtReceiver.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {
    @Mock
    AccommodationDomainEntityAdapter accommodationDomainEntityAdapter;

    @InjectMocks
    AccommodationServiceImpl accommodationService;
    @Mock
    AddressDomainEntityAdapter addressDomainEntityAdapter;

    @InjectMocks
    AddressServiceImpl addressService;

    @BeforeEach
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        addressService = new AddressServiceImpl(addressDomainEntityAdapter, objectMapper);
    }


    @Test
    void createAddressSuccessfully() {
        String name = "Hilton Belgrade";
        UserInfo userInfo = UserInfo.builder()
                .role(Role.HOST)
                .id(5L)
                .build();
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name(name)
                .accommodationType(AccommodationType.APARTMENT)
                .description("Nice accommodation in Center of city")
                .hostId(5L)
                .build();

        when(accommodationDomainEntityAdapter.save(accommodationDomain)).thenReturn(1L);

        ArgumentCaptor<AccommodationDomain> captor = ArgumentCaptor.forClass(AccommodationDomain.class);

        accommodationService.save(accommodationDomain, userInfo);

        verify(accommodationDomainEntityAdapter, times(1)).save(captor.capture());

        AccommodationDomain capturedAccommodation = captor.getValue();
        assertThat(capturedAccommodation).isEqualTo(accommodationDomain);

        AddressDomain addressDomain = AddressDomain.builder()
                .accommodation(accommodationDomain)
                .country("Serbia")
                .city("Belgrade")
                .postalCode("11000")
                .street("Bulevar Kralja Aleksandra")
                .streetNumber("113a")
                .build();

        when(addressDomainEntityAdapter.save(addressDomain)).thenReturn(1L);

        ArgumentCaptor<AddressDomain> captorUnit = ArgumentCaptor.forClass(AddressDomain.class);

        addressService.save(addressDomain);

        verify(addressDomainEntityAdapter, times(1)).save(captorUnit.capture());

        AddressDomain capturedAddress = captorUnit.getValue();
        assertThat(capturedAddress).isEqualTo(addressDomain);

    }

    @Test
    void findAddressById() {
        String name = "Fancy Accommodation";
        Long id = 1L;
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .id(id)
                .name(name)
                .accommodationType(AccommodationType.APARTMENT)
                .description("Nice accommodation in Center of city")
                .hostId(5L)
                .build();
        AddressDomain addressDomain = AddressDomain.builder()
                .id(1L)
                .accommodation(accommodationDomain)
                .country("Serbia")
                .city("Belgrade")
                .postalCode("11000")
                .street("Bulevar Kralja Aleksandra")
                .streetNumber("113a")
                .build();
        when(addressDomainEntityAdapter.findById(id)).thenReturn(Optional.of(addressDomain));

        AddressDomain actualAddress = addressService.findById(id);

        assertThat(actualAddress).isNotNull();
        assertThat(actualAddress.getId()).isEqualTo(id);
        assertThat(actualAddress.getCountry()).isEqualTo("Serbia");

    }

    @Test
    void findAddressWhenIdDoesNotExist() {
        Long id = 1L;
        when(addressDomainEntityAdapter.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> addressService.findById(id))
                .isInstanceOf(AddressNotFoundException.class)
                .hasMessage("Address with id: " + id + " doesn't exist");
    }



    @Test
    void updateAddressSuccessfully() {
        String name = "Fancy Accommodation";
        Long id = 1L;
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .id(id)
                .name(name)
                .accommodationType(AccommodationType.APARTMENT)
                .description("Nice accommodation in Center of city")
                .hostId(5L)
                .build();
        AddressDomain addressDomain = AddressDomain.builder()
                .id(1L)
                .accommodation(accommodationDomain)
                .country("Serbia")
                .city("Belgrade")
                .postalCode("11000")
                .street("Bulevar Kralja Aleksandra")
                .streetNumber("113a")
                .build();
        AddressDomainUpdate addressDomainUpdated = AddressDomainUpdate.builder()
                .id(1L)
                .postalCode("11000")
                .street("Bulevar Milutina Milankovica")
                .streetNumber("113")
                .build();

        when(addressDomainEntityAdapter.findById(id)).thenReturn(Optional.of(addressDomain));

        addressService.update(addressDomainUpdated);
        assertThat(addressDomain.getStreet()).isEqualTo("Bulevar Milutina Milankovica");
        verify(addressDomainEntityAdapter, times(1)).save(addressDomain);
    }

    @Test
    void updateAddressNoExistingId() {
        Long id = 1L;
        AddressDomainUpdate addressDomainUpdated = AddressDomainUpdate.builder()
                .id(1L)
                .postalCode("11000")
                .street("Bulevar Kralja Aleksandra")
                .streetNumber("113a")
                .build();

        when(addressDomainEntityAdapter.findById(id)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> addressService.update(addressDomainUpdated))
                .isInstanceOf(AddressNotFoundException.class)
                .hasMessage("Address with id: " + id + " doesn't exist");
        verify(addressDomainEntityAdapter, never()).save(any(AddressDomain.class));
    }
}