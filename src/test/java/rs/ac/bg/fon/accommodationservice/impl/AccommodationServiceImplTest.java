package rs.ac.bg.fon.accommodationservice.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.AccommodationDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AccommodationDomainUpdate;
import rs.ac.bg.fon.accommodationservice.exception.specific.AccommodationNotFoundException;
import rs.ac.bg.fon.accommodationservice.exception.specific.UserNotOwnerOfAccommodationException;
import rs.ac.bg.fon.accommodationservice.exception.specific.WrongAccessRoleException;
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
import rs.ac.bg.fon.accommodationservice.service.AccommodationUnitService;
import rs.ac.bg.fon.accommodationservice.service.AddressService;
import rs.ac.bg.fon.accommodationservice.service.impl.AccommodationServiceImpl;
import rs.ac.bg.fon.accommodationservice.service.impl.AccommodationUnitServiceImpl;
import rs.ac.bg.fon.accommodationservice.service.impl.AddressServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceImplTest {

    @Mock
    AccommodationDomainEntityAdapter accommodationDomainEntityAdapter;

    @InjectMocks
    AccommodationServiceImpl accommodationService;

    @InjectMocks
    AccommodationUnitServiceImpl accommodationUnitService;

    @InjectMocks
    AddressServiceImpl addressService;

    @BeforeEach
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        accommodationService = new AccommodationServiceImpl(accommodationDomainEntityAdapter, objectMapper,accommodationUnitService,addressService);
    }


    @Test
    void createAccommodationSuccessfully() {
        UserInfo userInfo = UserInfo.builder()
                .role(Role.HOST)
                .id(3L)
                .build();
        String name = "Hilton Belgrade";
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name(name)
                .accommodationType(AccommodationType.APARTMENT)
                .description("Nice accommodation in Center of city")
                .hostId(3L)
                .build();

        when(accommodationDomainEntityAdapter.save(accommodationDomain)).thenReturn(1L);

        ArgumentCaptor<AccommodationDomain> captor = ArgumentCaptor.forClass(AccommodationDomain.class);

        accommodationService.save(accommodationDomain,userInfo);

        verify(accommodationDomainEntityAdapter, times(1)).save(captor.capture());

        AccommodationDomain capturedAccommodation = captor.getValue();
        assertThat(capturedAccommodation).isEqualTo(accommodationDomain);
        assertThat(capturedAccommodation.getHostId()).isEqualTo(userInfo.getId());
    }

    @Test
    void findAccommodationById() {
        String name = "Fancy Accommodation";
        Long id = 1L;
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .id(id)
                .name(name)
                .accommodationType(AccommodationType.APARTMENT)
                .description("Nice accommodation in Center of city")
                .hostId(5L)
                .build();

        when(accommodationDomainEntityAdapter.findById(id)).thenReturn(Optional.of(accommodationDomain));

        AccommodationDomain actual = accommodationService.findById(id);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo(name);

    }

    @Test
    void findAccommodationWhenIdDoesNotExist() {
        Long id = 1L;
        when(accommodationDomainEntityAdapter.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> accommodationService.findById(id))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessage("Accommodation with id: " + id + " doesn't exist");
    }




    @Test
    void updateAccommodationDetailsSuccessfully() {
        String name = "Fancy Accommodation";
        String newName = "Hilton Belgrade";
        Long id = 1L;
        UserInfo userInfo = UserInfo.builder()
                .role(Role.HOST)
                .id(5L)
                .build();
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .id(id)
                .name(name)
                .accommodationType(AccommodationType.APARTMENT)
                .description("Nice accommodation in Center of city")
                .hostId(5L)
                .build();
        AccommodationDomainUpdate accommodationDomainUpdated = AccommodationDomainUpdate.builder()
                .id(id)
                .name(newName)
                .accommodationType(AccommodationType.HOTEL)
                .description("Nice accommodation in Center of city")
                .build();

        when(accommodationDomainEntityAdapter.findById(id)).thenReturn(Optional.of(accommodationDomain));

        accommodationService.update(accommodationDomainUpdated,userInfo);
        assertThat(accommodationDomain.getName()).isEqualTo(newName);
        verify(accommodationDomainEntityAdapter, times(1)).save(accommodationDomain);
    }

    @Test
    void updateAccommodation_hostIsNotOwnerOfAccommodation() {
        String name = "Fancy Accommodation";
        String newName = "Hilton Belgrade";
        Long id = 1L;
        UserInfo userInfo = UserInfo.builder()
                .role(Role.HOST)
                .id(3L)
                .build();
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .id(id)
                .name(name)
                .accommodationType(AccommodationType.APARTMENT)
                .description("Nice accommodation in Center of city")
                .hostId(5L)
                .build();
        AccommodationDomainUpdate accommodationDomainUpdated = AccommodationDomainUpdate.builder()
                .id(id)
                .name(newName)
                .accommodationType(AccommodationType.HOTEL)
                .description("Nice accommodation in Center of city")
                .build();

        when(accommodationDomainEntityAdapter.findById(id)).thenReturn(Optional.of(accommodationDomain));

        assertThatThrownBy(() -> accommodationService.update(accommodationDomainUpdated, userInfo))
                .isInstanceOf(UserNotOwnerOfAccommodationException.class)
                .hasMessage("Host with id " + userInfo.getId() + " is not assigned as owner of accommodation with id " + id);
        verify(accommodationDomainEntityAdapter, never()).save(accommodationDomain);
    }
    @Test
    void updateAccommodationDetailsNoExistingId() {
        UserInfo userInfo = UserInfo.builder()
                .role(Role.HOST)
                .id(5L)
                .build();
        String newName = "Hilton Belgrade";
        Long id = 1L;
        AccommodationDomainUpdate accommodationDomainUpdated = AccommodationDomainUpdate.builder()
                .id(id)
                .name(newName)
                .accommodationType(AccommodationType.HOTEL)
                .description("Nice accommodation in Center of city")
                .build();

        when(accommodationDomainEntityAdapter.findById(id)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> accommodationService.update(accommodationDomainUpdated,userInfo))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessage("Accommodation with id: " + id + " doesn't exist");
        verify(accommodationDomainEntityAdapter, never()).save(any(AccommodationDomain.class));
    }


}