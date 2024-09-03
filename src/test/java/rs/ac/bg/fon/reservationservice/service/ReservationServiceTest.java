package rs.ac.bg.fon.reservationservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import rs.ac.bg.fon.reservationservice.adapters.ReservationDomainEntityAdapter;
import rs.ac.bg.fon.reservationservice.domain.ReservationDomain;
import rs.ac.bg.fon.reservationservice.domain.SearchReservationDomain;
import rs.ac.bg.fon.reservationservice.exceptions.*;
import rs.ac.bg.fon.reservationservice.feignclient.AccommodationClient;
import rs.ac.bg.fon.reservationservice.feignclient.Price;
import rs.ac.bg.fon.reservationservice.model.DateRange;
import rs.ac.bg.fon.reservationservice.model.ProfileInfo;
import rs.ac.bg.fon.reservationservice.model.ReservationStatus;
import rs.ac.bg.fon.reservationservice.model.Role;
import rs.ac.bg.fon.reservationservice.service.impl.ReservationServiceImpl;
import rs.ac.bg.fon.reservationservice.util.JwtUtil;
import rs.ac.bg.fon.reservationservice.util.WireMockTestHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    JwtUtil jwtUtil;
    @Mock
    ReservationDomainEntityAdapter reservationDomainEntityAdapter;

    @BeforeAll
    public static void setUpBeforeAll() {
        WireMockTestHelper.start();
    }

    @BeforeEach
    public void setUp() {
        WireMockTestHelper.setUp();
        ProfileInfo profileInfo = ProfileInfo.builder().role(Role.USER).id(1L).build();
        lenient().when(jwtUtil.getFromJwt(jwtUser)).thenReturn(profileInfo);
    }

    @AfterAll
    public static void tearDown() {
        WireMockTestHelper.stop();
    }

    @InjectMocks
    ReservationServiceImpl reservationServiceImpl;

    @Mock
    AccommodationClient accommodationClient;


    private static final String jwtUser = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiVVNFUiIsImlkIjoxfQ.qXSBl3zLpQkczPhMxITg1p45w7VsTiGKr3ZoHjs49P0";
    private static final String jwtHost = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiSE9TVCIsImlkIjoxfQ.tidRtPDpV1NcBOVFJsHWaub1afEeSFF39_guFCxRDk0";


    @Test
    void save_expectSuccess() {
        ReservationDomain reservationDomain = newReservationDomain();

        LocalDate checkIn = LocalDate.of(2024, 5, 10);
        LocalDate checkOut = LocalDate.of(2024, 5, 12);

        when(reservationDomainEntityAdapter.getAllExistingReservationsBetweenDates(checkIn, checkOut, 1L))
                .thenReturn(Collections.emptyList());
        when(accommodationClient.getAllPrices(1L, checkIn, checkOut)).thenReturn(Collections.singletonList(newPrice()));

        when(reservationDomainEntityAdapter.save(any())).thenReturn(1L);

        Long savedReservationId = reservationServiceImpl.save(reservationDomain);


        assertEquals(1L, savedReservationId);
        verify(reservationDomainEntityAdapter, times(1)).getAllExistingReservationsBetweenDates(checkIn, checkOut, 1L);
        verify(reservationDomainEntityAdapter, times(1)).save(any());
    }

    @Test
    void save_noPricesAreDefined_expectException() {
        ReservationDomain reservationDomain = newReservationDomain();

        LocalDate checkIn = LocalDate.of(2024, 5, 10);
        LocalDate checkOut = LocalDate.of(2024, 5, 12);

        when(reservationDomainEntityAdapter.getAllExistingReservationsBetweenDates(checkIn, checkOut, 1L))
                .thenReturn(Collections.emptyList());
        when(accommodationClient.getAllPrices(1L, checkIn, checkOut)).thenReturn(Collections.emptyList());


        assertThrows(NotDefinedPricesForDatesException.class, () -> reservationServiceImpl.save(reservationDomain));
        verify(reservationDomainEntityAdapter, times(1)).getAllExistingReservationsBetweenDates(checkIn, checkOut, 1L);

    }

    @Test
    void save_defineMissingPricesForSomeDates_expectException() {
        ReservationDomain reservationDomain = newReservationDomain();

        LocalDate checkIn = LocalDate.of(2024, 5, 10);
        LocalDate checkOut = LocalDate.of(2024, 5, 12);

        Price price = newPrice();
        price.setDateFrom(LocalDate.of(2024, 5, 11));
        price.setDateTo(LocalDate.of(2024, 5, 17));
        when(reservationDomainEntityAdapter.getAllExistingReservationsBetweenDates(checkIn, checkOut, 1L))
                .thenReturn(Collections.emptyList());
        when(accommodationClient.getAllPrices(1L, checkIn, checkOut)).thenReturn(Collections.singletonList(price));

        assertThrows(NotDefinedPricesForDatesException.class, () -> reservationServiceImpl.save(reservationDomain));
        verify(reservationDomainEntityAdapter, times(1)).getAllExistingReservationsBetweenDates(checkIn, checkOut, 1L);

    }


    @Test
    public void reserveOnAlreadyReservedDates_expectSaveToFail() {
        ReservationDomain reservationDomain = newReservationDomain();
        reservationDomain.setId(2L);

        LocalDate checkIn = LocalDate.of(2024, 5, 10);
        LocalDate checkOut = LocalDate.of(2024, 5, 12);

        when(reservationDomainEntityAdapter.getAllExistingReservationsBetweenDates(checkIn, checkOut, 1L))
                .thenReturn(Collections.singletonList(newReservationDomain()));

        assertThrows(ReservationAlreadyExistsException.class, () -> reservationServiceImpl.save(reservationDomain));
        verify(reservationDomainEntityAdapter, times(1)).getAllExistingReservationsBetweenDates(checkIn, checkOut, 1L);
        verify(reservationDomainEntityAdapter, never()).save(any());
    }


    @Test
    void getById_expectSuccess() {
        ReservationDomain expectedReservationDomain = newReservationDomain();
        when(reservationDomainEntityAdapter.getById(expectedReservationDomain.getId())).thenReturn(Optional.of(expectedReservationDomain));

        ReservationDomain actualReservationDomain = reservationServiceImpl.getById(expectedReservationDomain.getId());

        assertEquals(expectedReservationDomain, actualReservationDomain);
    }

    @Test
    void getById_ifNotFound_expectException() {
        Long id = 1L;

        when(reservationDomainEntityAdapter.getById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationServiceImpl.getById(id));
        verify(reservationDomainEntityAdapter, times(1)).getById(id);
    }

    @Test
    void getAll_expectSuccess() {
        Page<ReservationDomain> expectedPage = Page.empty();
        when(reservationDomainEntityAdapter.getAll(any(Pageable.class))).thenReturn(expectedPage);

        Pageable pageable = PageRequest.of(0, 1);
        Page<ReservationDomain> actualPage = reservationServiceImpl.getAll(pageable);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    void delete_expectSuccess() {
        reservationServiceImpl.delete(1L);

        verify(reservationDomainEntityAdapter, times(1)).delete(1L);
    }

    @Test
    public void getAllByProfileId_whenBothCheckInAndCheckOutAreNull_expectAllReservations() {
        Long profileId = 1L;
        Pageable pageable = Pageable.unpaged();
        ReservationDomain reservationDomain = newReservationDomain();

        reservationDomain.getDateRange().setCheckInDate(LocalDate.of(1900, 1, 1));
        reservationDomain.getDateRange().setCheckOutDate(LocalDate.of(5000, 1, 1));

        List<ReservationDomain> list = new ArrayList<>();
        list.add(newReservationDomain());

        Page<ReservationDomain> expectedPage = new PageImpl<>(list);


        when(reservationDomainEntityAdapter.getAllByProfileIdBetween(reservationDomain.getDateRange().getCheckInDate(), reservationDomain.getDateRange().getCheckOutDate(), profileId, pageable)).thenReturn(expectedPage);

        SearchReservationDomain searchReservationDomain = SearchReservationDomain.builder().checkInDate(reservationDomain.getDateRange().getCheckInDate())
                .checkOutDate(reservationDomain.getDateRange().getCheckOutDate())
                .pageable(pageable).profileId(profileId).build();


        reservationDomain.getDateRange().setCheckInDate(null);
        reservationDomain.getDateRange().setCheckOutDate(null);
        Page<ReservationDomain> resultPage = reservationServiceImpl.getAllByProfileId(searchReservationDomain, jwtUser);

        assertEquals(expectedPage, resultPage);
    }

    @Test
    public void getAllByProfileId_whenCheckInIsNull_expectAllReservationsBeforeThatDate() {

        Long profileId = 1L;
        Pageable pageable = Pageable.unpaged();
        ReservationDomain reservationDomain = newReservationDomain();
        reservationDomain.getDateRange().setCheckInDate(LocalDate.of(1900, 1, 1));

        List<ReservationDomain> list = new ArrayList<>();
        list.add(newReservationDomain());

        Page<ReservationDomain> expectedPage = new PageImpl<>(list);


        when(reservationDomainEntityAdapter.getAllByProfileIdBetween(reservationDomain.getDateRange().getCheckInDate(), reservationDomain.getDateRange().getCheckOutDate(), profileId, pageable)).thenReturn(expectedPage);
        SearchReservationDomain searchReservationDomain = SearchReservationDomain.builder().checkInDate(reservationDomain.getDateRange().getCheckInDate())
                .checkOutDate(reservationDomain.getDateRange().getCheckOutDate())
                .pageable(pageable).profileId(profileId).build();

        reservationDomain.getDateRange().setCheckInDate(null);
        Page<ReservationDomain> resultPage = reservationServiceImpl.getAllByProfileId(searchReservationDomain, jwtUser);


        assertEquals(expectedPage, resultPage);
    }

    @Test
    public void getAllByProfileId_whenCheckOutIsNull_expectAllReservationsAfterThatDate() {
        Long profileId = 1L;
        Pageable pageable = Pageable.unpaged();
        ReservationDomain reservationDomain = newReservationDomain();
        reservationDomain.getDateRange().setCheckInDate(LocalDate.of(2023, 1, 1));
        reservationDomain.getDateRange().setCheckOutDate(LocalDate.of(5000, 1, 1));

        List<ReservationDomain> list = new ArrayList<>();
        list.add(newReservationDomain());

        Page<ReservationDomain> expectedPage = new PageImpl<>(list);


        when(reservationDomainEntityAdapter.getAllByProfileIdBetween(reservationDomain.getDateRange().getCheckInDate(), reservationDomain.getDateRange().getCheckOutDate(), profileId, pageable)).thenReturn(expectedPage);

        SearchReservationDomain searchReservationDomain = SearchReservationDomain.builder().checkInDate(reservationDomain
                        .getDateRange().getCheckInDate())
                .checkOutDate(reservationDomain.getDateRange().getCheckOutDate())
                .pageable(pageable).profileId(profileId).build();


        reservationDomain.getDateRange().setCheckOutDate(null);
        Page<ReservationDomain> resultPage = reservationServiceImpl.getAllByProfileId(searchReservationDomain, jwtUser);


        assertEquals(expectedPage, resultPage);
    }

    @Test
    public void getAllByProfileId_whenBothCheckInAndCheckOutAreProvided_expectAllReservationBetweenThoseDates() {

        Long profileId = 1L;
        Pageable pageable = Pageable.unpaged();
        ReservationDomain reservationDomain = newReservationDomain();
        ReservationDomain reservationDomain2 = newReservationDomain();

        reservationDomain2.getDateRange().setCheckInDate(LocalDate.now());
        reservationDomain2.getDateRange().setCheckOutDate(LocalDate.now().plusDays(30));


        List<ReservationDomain> list = new ArrayList<>();
        list.add(newReservationDomain());

        Page<ReservationDomain> expectedPage = new PageImpl<>(list);


        when(reservationDomainEntityAdapter.getAllByProfileIdBetween(reservationDomain.getDateRange().getCheckInDate(), reservationDomain2.getDateRange().getCheckOutDate(), profileId, pageable)).thenReturn(expectedPage);

        SearchReservationDomain searchReservationDomain = SearchReservationDomain.builder().checkInDate(reservationDomain.getDateRange()
                        .getCheckInDate())
                .checkOutDate(reservationDomain2.getDateRange().getCheckOutDate())
                .pageable(pageable).profileId(profileId).build();


        Page<ReservationDomain> resultPage = reservationServiceImpl.getAllByProfileId(searchReservationDomain, jwtUser);


        assertEquals(expectedPage, resultPage);
    }

    @Test
    public void getAllByHostId_emptyList() {
        Pageable pageable = Pageable.unpaged();
        Page<ReservationDomain> expectedPage = new PageImpl<>(new ArrayList<>());

        when(jwtUtil.getFromJwt(jwtHost)).thenReturn(ProfileInfo.builder().role(Role.HOST).build());
        when(accommodationClient.getAccommodationUnitsByHostId(jwtHost)).thenReturn(new ObjectMapper().createObjectNode());
        when(reservationDomainEntityAdapter.getAllByUnitsIdBetween(LocalDate.now(), LocalDate.now().plusDays(1), List.of(), pageable)).thenReturn(expectedPage);


        SearchReservationDomain searchReservationDomain = SearchReservationDomain.builder()
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(1))
                .pageable(pageable).profileId(1L).build();


        Page<ReservationDomain> resultPage = reservationServiceImpl.getAllByProfileId(searchReservationDomain, jwtHost);

        assertEquals(expectedPage, resultPage);
    }

    @Test
    public void getAllByHostId_nonEmptyList() {
        Pageable pageable = Pageable.unpaged();
        ReservationDomain reservationDomain = newReservationDomain();

        List<ReservationDomain> list = new ArrayList<>();
        list.add(reservationDomain);
        Page<ReservationDomain> expectedPage = new PageImpl<>(list);

        when(jwtUtil.getFromJwt(jwtHost)).thenReturn(ProfileInfo.builder().role(Role.HOST).build());
        JsonNode node = new ObjectMapper().valueToTree(List.of(ProfileInfo.builder().id(1L).build()));
        when(accommodationClient.getAccommodationUnitsByHostId(jwtHost)).thenReturn(node);
        when(reservationDomainEntityAdapter.getAllByUnitsIdBetween(LocalDate.now(), LocalDate.now().plusDays(1), List.of(1L), pageable)).thenReturn(expectedPage);


        SearchReservationDomain searchReservationDomain = SearchReservationDomain.builder()
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(1))
                .pageable(pageable).profileId(1L).build();


        Page<ReservationDomain> resultPage = reservationServiceImpl.getAllByProfileId(searchReservationDomain, jwtHost);

        assertEquals(expectedPage, resultPage);
    }

    @Test
    void cancelReservation_expectCancelled() {
        Long reservationId = 1L;
        ReservationDomain reservationDomain = newReservationDomain();

        when(reservationDomainEntityAdapter.getById(reservationId)).thenReturn(Optional.of(reservationDomain));
        reservationServiceImpl.cancelReservation(reservationId, 1L);

        verify(reservationDomainEntityAdapter).getById(reservationId);
        verify(reservationDomainEntityAdapter).update(reservationDomain);
        assertEquals(reservationDomain.getStatus(), ReservationStatus.CANCELED);
    }

    @Test
    void cancelReservation_expectException() {
        Long reservationId = 1L;
        ReservationDomain reservationDomain = newReservationDomain();
        reservationDomain.setStatus(ReservationStatus.CANCELED);

        when(reservationDomainEntityAdapter.getById(reservationId)).thenReturn(Optional.of(reservationDomain));
        assertThrows(ReservationNotLongerExistsException.class, () -> reservationServiceImpl.cancelReservation(reservationId, 1L));
    }

    @Test
    void cancelReservation_expectExceptionUnauthorized() {
        Long reservationId = 1L;
        ReservationDomain reservationDomain = newReservationDomain();
        reservationDomain.setStatus(ReservationStatus.ACTIVE);

        when(reservationDomainEntityAdapter.getById(reservationId)).thenReturn(Optional.of(reservationDomain));
        assertThrows(CancelReservationUnauthorizedException.class, () -> reservationServiceImpl.cancelReservation(reservationId, 2L));
    }

    @Test
    void processPayment_expectSuccess() {
        Long reservationId = 1L;
        ReservationDomain reservation = newReservationDomain();
        reservation.setId(reservationId);

        when(reservationDomainEntityAdapter.getById(reservationId)).thenReturn(Optional.of(reservation));

        assertDoesNotThrow(() -> reservationServiceImpl.processPayment(reservationId));

        verify(reservationDomainEntityAdapter, times(1)).getById(reservationId);
        verify(reservationDomainEntityAdapter, times(1)).save(reservation);
        assertThat(reservation.isPaid()).isTrue();
    }

    @Test
    void processPayment_expectException() {
        Long reservationId = 1L;
        ReservationDomain reservation = newReservationDomain();
        reservation.setId(reservationId);
        reservation.setPaid(true);

        when(reservationDomainEntityAdapter.getById(reservationId)).thenReturn(Optional.of(reservation));


        assertThatThrownBy(() -> reservationServiceImpl.processPayment(reservationId))
                .isInstanceOf(ReservationAlreadyPaidException.class)
                .hasMessage("Reservation is already paid");
        verify(reservationDomainEntityAdapter, times(1)).getById(reservationId);
        verify(reservationDomainEntityAdapter, never()).save(reservation);
    }

    Price newPrice() {
        return Price
                .builder()
                .amount(new BigDecimal(50))
                .dateFrom(LocalDate.of(2024, 5, 10))
                .dateTo(LocalDate.of(2024, 5, 12))
                .build();
    }

    ReservationDomain newReservationDomain() {

        return ReservationDomain
                .builder()
                .id(1L)
                .creationDate(LocalDate.now())
                .status(ReservationStatus.ACTIVE)
                .totalAmount(BigDecimal.ONE)
                .dateRange(new DateRange(LocalDate.of(2024, 5, 10), LocalDate.of(2024, 5, 12)))
                .numberOfPeople(1)
                .accommodationUnitId(1L)
                .profileId(1L)
                .build();
    }

}

