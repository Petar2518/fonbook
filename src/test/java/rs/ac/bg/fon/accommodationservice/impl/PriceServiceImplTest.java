package rs.ac.bg.fon.accommodationservice.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.AccommodationDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.AccommodationUnitDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.PriceDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.constraint.validator.DateValidator;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.accommodationservice.domain.PriceDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.PriceDomainUpdate;
import rs.ac.bg.fon.accommodationservice.exception.specific.DateUnavailableException;
import rs.ac.bg.fon.accommodationservice.exception.specific.PriceNotFoundException;
import rs.ac.bg.fon.accommodationservice.mapper.AccommodationUnitMapper;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.JwtReceiver.Role;
import rs.ac.bg.fon.accommodationservice.model.JwtReceiver.UserInfo;
import rs.ac.bg.fon.accommodationservice.model.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.ac.bg.fon.accommodationservice.service.impl.AccommodationServiceImpl;
import rs.ac.bg.fon.accommodationservice.service.impl.AccommodationUnitServiceImpl;
import rs.ac.bg.fon.accommodationservice.service.impl.PriceServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceImplTest {

    @Mock
    AccommodationUnitDomainEntityAdapter accommodationUnitDomainEntityAdapter;

    @Mock
    AccommodationDomainEntityAdapter accommodationDomainEntityAdapter;

    @Mock
    PriceDomainEntityAdapter priceDomainEntityAdapter;

    @Mock
    AccommodationUnitMapper accommodationUnitMapper;

    @InjectMocks
    PriceServiceImpl priceService;
    @InjectMocks
    AccommodationServiceImpl accommodationService;

    @InjectMocks
    AccommodationUnitServiceImpl accommodationUnitService;

    @BeforeEach
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        priceService = new PriceServiceImpl(priceDomainEntityAdapter, objectMapper);
    }

    @Test
    void createPriceSuccessfully() {
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

        PriceDomain priceDomain = PriceDomain.builder()
                .dateFrom(LocalDate.of(2025, 3, 24))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(110.00))
                .currency("Euros")
                .accommodationUnit(accommodationUnitDomain)
                .build();

        when(priceDomainEntityAdapter.save(priceDomain)).thenReturn(1L);

        ArgumentCaptor<PriceDomain> captorPrice = ArgumentCaptor.forClass(PriceDomain.class);

        priceService.save(priceDomain);

        verify(priceDomainEntityAdapter, times(1)).save(captorPrice.capture());

        PriceDomain capturedPrice = captorPrice.getValue();
        assertThat(capturedPrice).isEqualTo(priceDomain);

    }

    @Test
    void createPriceForOccupiedDate() {
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

        PriceDomain priceDomain = PriceDomain.builder()
                .dateFrom(LocalDate.of(2025, 3, 22))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDomain)
                .build();

        when(priceDomainEntityAdapter.save(priceDomain)).thenReturn(1L);

        ArgumentCaptor<PriceDomain> captorPrice = ArgumentCaptor.forClass(PriceDomain.class);

        priceService.save(priceDomain);

        verify(priceDomainEntityAdapter, times(1)).save(captorPrice.capture());

        PriceDomain capturedPrice = captorPrice.getValue();
        assertThat(capturedPrice).isEqualTo(priceDomain);

        LocalDate from = LocalDate.of(2025, 3, 24);
        LocalDate to = LocalDate.of(2025, 3, 25);
        List<PriceDomain> pricedomainList = new ArrayList<>();
        pricedomainList.add(priceDomain);
        PriceDomain priceDomain2 = PriceDomain.builder()
                .dateFrom(from)
                .dateTo(to)
                .amount(BigDecimal.valueOf(130.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDomain)
                .build();

        when(priceDomainEntityAdapter.findPricesForDatesForAccommodationUnit(accommodationUnitDomain.getId(), from, to)).thenReturn(pricedomainList);

        assertThatThrownBy(() -> priceService.save(priceDomain2))
                .isInstanceOf(DateUnavailableException.class)
                .hasMessage("Price is already added for that date");


    }

    @Test
    void findPriceById() {
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

        PriceDomain priceDomain = PriceDomain.builder()
                .id(id)
                .dateFrom(LocalDate.of(2025, 3, 24))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDomain)
                .build();

        when(priceDomainEntityAdapter.findById(id)).thenReturn(Optional.of(priceDomain));

        PriceDomain actualUnit = priceService.findById(id);

        assertThat(actualUnit).isNotNull();
        assertThat(actualUnit.getId()).isEqualTo(id);
        assertThat(actualUnit.getAmount()).isEqualTo(BigDecimal.valueOf(110.00));


    }

    @Test
    void findPriceWhenIdDoesNotExist() {
        Long id = 1L;
        when(priceDomainEntityAdapter.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> priceService.findById(id))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessage("Price with id: " + id + " doesn't exist");
    }




    @Test
    void updatePriceDetailsSuccessfully() {
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

        PriceDomain priceDomain = PriceDomain.builder()
                .id(id)
                .dateFrom(LocalDate.of(2025, 3, 24))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDomain)
                .build();
        PriceDomainUpdate priceDomainUpdated = PriceDomainUpdate.builder()
                .id(id)
                .dateFrom(LocalDate.of(2025, 4, 24))
                .dateTo(LocalDate.of(2025, 4, 26))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .build();


        when(priceDomainEntityAdapter.findById(id)).thenReturn(Optional.of(priceDomain));

        priceService.update(priceDomainUpdated);
        assertThat(priceDomain.getAmount()).isEqualTo(BigDecimal.valueOf(120.00));
        assertThat(priceDomain.getDateFrom()).isEqualTo(LocalDate.of(2025, 4, 24));
        assertThat(priceDomain.getDateTo()).isEqualTo(LocalDate.of(2025, 4, 26));
        verify(priceDomainEntityAdapter, times(1)).save(priceDomain);

    }

    @Test
    void updatePriceDetailsNoExistingId() {
        String newName = "Hilton Belgrade";
        String unitName = "Hilton Belgrade room";
        Long id = 1L;
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .id(id)
                .name(newName)
                .accommodationType(AccommodationType.HOTEL)
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
        PriceDomainUpdate priceDomainUpdated = PriceDomainUpdate.builder()
                .id(id)
                .dateFrom(LocalDate.of(2025, 4, 24))
                .dateTo(LocalDate.of(2025, 4, 26))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .build();

        when(priceDomainEntityAdapter.findById(id)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> priceService.update(priceDomainUpdated))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessage("Price with id: " + id + " doesn't exist");
        verify(priceDomainEntityAdapter, never()).save(any(PriceDomain.class));
    }

    @Test
    void createPriceInvalidDates() {
        String newName = "Hilton Belgrade";
        String unitName = "Hilton Belgrade room";
        Long id = 1L;
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .id(id)
                .name(newName)
                .accommodationType(AccommodationType.HOTEL)
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
        PriceDomain priceDomainUpdated = PriceDomain.builder()
                .id(id)
                .dateFrom(LocalDate.of(2025, 4, 28))
                .dateTo(LocalDate.of(2025, 4, 26))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDomain)
                .build();

        Price priceUpdated = Price.builder()
                .id(id)
                .dateFrom(LocalDate.of(2025, 4, 28))
                .dateTo(LocalDate.of(2025, 4, 26))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitMapper.domainToEntity(accommodationUnitDomain))
                .build();

        DateValidator validator = new DateValidator();

        boolean isValid = validator.isValid(priceUpdated, null);

        assertFalse(isValid, "Date From field needs to be before Date To field");

    }

}