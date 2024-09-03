package rs.ac.bg.fon.accommodationservice.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.AccommodationDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.AccommodationUnitDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AccommodationUnitDomainUpdate;
import rs.ac.bg.fon.accommodationservice.exception.specific.AccommodationUnitNotFoundException;
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
import rs.ac.bg.fon.accommodationservice.service.PriceService;
import rs.ac.bg.fon.accommodationservice.service.impl.AccommodationServiceImpl;
import rs.ac.bg.fon.accommodationservice.service.impl.AccommodationUnitServiceImpl;
import rs.ac.bg.fon.accommodationservice.service.impl.PriceServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccommodationUnitServiceImplTest {

    @Mock
    AccommodationUnitDomainEntityAdapter accommodationUnitDomainEntityAdapter;

    @Mock
    AccommodationDomainEntityAdapter accommodationDomainEntityAdapter;

    @InjectMocks
    AccommodationServiceImpl accommodationService;

    @InjectMocks
    AccommodationUnitServiceImpl accommodationUnitService;

    @InjectMocks
    PriceServiceImpl priceService;

    @BeforeEach
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        accommodationUnitService = new AccommodationUnitServiceImpl(accommodationUnitDomainEntityAdapter,priceService, objectMapper);
    }

    @Test
    void createAccommodationUnitSuccessfully() {

        UserInfo userInfo = UserInfo.builder()
                .role(Role.HOST)
                .id(5L)
                .build();

        String accommodationName = "Hilton Belgrade";
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name(accommodationName)
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

        AccommodationUnitDomain accommodationUnitDomain = AccommodationUnitDomain.builder()
                .accommodation(accommodationDomain)
                .name("King size bedroom")
                .description("Large room")
                .capacity(2)
                .build();

        when(accommodationUnitDomainEntityAdapter.save(accommodationUnitDomain)).thenReturn(1L);

        ArgumentCaptor<AccommodationUnitDomain> captorUnit = ArgumentCaptor.forClass(AccommodationUnitDomain.class);

        accommodationUnitService.save(accommodationUnitDomain);

        verify(accommodationUnitDomainEntityAdapter, times(1)).save(captorUnit.capture());

        AccommodationUnitDomain capturedUnit = captorUnit.getValue();
        assertThat(capturedUnit).isEqualTo(accommodationUnitDomain);


    }

    @Test
    void findAccommodationUnitById() {
        String name = "Fancy Accommodation";
        String unitName = "King size bedroom";
        Long id = 1L;
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .id(id)
                .name(name)
                .accommodationType(AccommodationType.APARTMENT)
                .description("Nice accommodation in Center of city")
                .hostId(5L)
                .build();

        AccommodationUnitDomain accommodationUnitDomain = AccommodationUnitDomain.builder()
                .id(id)
                .accommodation(accommodationDomain)
                .name(unitName)
                .description("Large room")
                .capacity(2)
                .build();

        when(accommodationUnitDomainEntityAdapter.findById(id)).thenReturn(Optional.of(accommodationUnitDomain));

        AccommodationUnitDomain actualUnit = accommodationUnitService.findById(id);

        assertThat(actualUnit).isNotNull();
        assertThat(actualUnit.getId()).isEqualTo(id);
        assertThat(actualUnit.getName()).isEqualTo(unitName);


    }

    @Test
    void findAccommodationUnitWhenIdDoesNotExist() {
        Long id = 1L;
        when(accommodationUnitDomainEntityAdapter.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> accommodationUnitService.findById(id))
                .isInstanceOf(AccommodationUnitNotFoundException.class)
                .hasMessage("Accommodation unit with id: " + id + " doesn't exist");
    }



    @Test
    void updateAccommodationUnitDetailsSuccessfully() {
        String name = "Fancy Accommodation";
        String unitName = "Hilton Belgrade room";
        String newUnitName = "King size room";
        Long id = 1L;
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .id(id)
                .name(name)
                .accommodationType(AccommodationType.APARTMENT)
                .description("Nice accommodation in Center of city")
                .hostId(5L)
                .build();

        AccommodationUnitDomain accommodationUnitDomain = AccommodationUnitDomain.builder()
                .id(id)
                .accommodation(accommodationDomain)
                .name(unitName)
                .description("Large room")
                .capacity(2)
                .build();

        AccommodationUnitDomainUpdate accommodationUnitDomainUpdated = AccommodationUnitDomainUpdate.builder()
                .id(id)
                .name(newUnitName)
                .description("Very large room")
                .capacity(22)
                .build();

        when(accommodationUnitDomainEntityAdapter.findById(id)).thenReturn(Optional.of(accommodationUnitDomain));

        accommodationUnitService.update(accommodationUnitDomainUpdated);
        assertThat(accommodationUnitDomain.getName()).isEqualTo(newUnitName);
        verify(accommodationUnitDomainEntityAdapter, times(1)).save(accommodationUnitDomain);

    }

    @Test
    void updateAccommodationDetailsNoExistingId() {
        String unitName = "Hilton Belgrade room";
        Long id = 1L;
        AccommodationUnitDomainUpdate accommodationUnitDomainUpdate = AccommodationUnitDomainUpdate.builder()
                .id(id)
                .name(unitName)
                .description("Large room")
                .capacity(2)
                .build();

        when(accommodationUnitDomainEntityAdapter.findById(id)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> accommodationUnitService.update(accommodationUnitDomainUpdate))
                .isInstanceOf(AccommodationUnitNotFoundException.class)
                .hasMessage("Accommodation unit with id: " + id + " doesn't exist");
        verify(accommodationUnitDomainEntityAdapter, never()).save(any(AccommodationUnitDomain.class));
    }


    @Test
    void getAllByHost() {

        Long id = 1L;
        Long hostId = 5L;
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .id(id)
                .name("Accommodation")
                .accommodationType(AccommodationType.APARTMENT)
                .description("Nice accommodation in Center of city")
                .hostId(hostId)
                .build();

        AccommodationUnitDomain room1 = AccommodationUnitDomain.builder()
                .id(id)
                .accommodation(accommodationDomain)
                .name("New room 1")
                .description("New room 1")
                .capacity(2)
                .build();
        AccommodationUnitDomain room2 = AccommodationUnitDomain.builder()
                .id(id)
                .accommodation(accommodationDomain)
                .name("New room 2")
                .description("New room 2")
                .capacity(2)
                .build();

        when(accommodationUnitDomainEntityAdapter.findAllByHostId(hostId, null)).thenReturn(List.of(room1, room2));

        List<AccommodationUnitDomain> rooms = accommodationUnitService.getAllByHost(hostId, null);

        assertThat(rooms).isNotEmpty();
        assertThat(rooms).contains(room1, room2);
    }
}