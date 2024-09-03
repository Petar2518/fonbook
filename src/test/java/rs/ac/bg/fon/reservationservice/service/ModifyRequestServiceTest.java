package rs.ac.bg.fon.reservationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import rs.ac.bg.fon.reservationservice.adapters.ReservationDateSettingDomainEntityAdapter;
import rs.ac.bg.fon.reservationservice.domain.*;
import rs.ac.bg.fon.reservationservice.model.DateRange;
import rs.ac.bg.fon.reservationservice.model.RequestStatus;
import rs.ac.bg.fon.reservationservice.model.ReservationStatus;
import rs.ac.bg.fon.reservationservice.service.impl.ReservationDateSettingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModifyRequestServiceTest {

    @Mock
    ReservationDateSettingDomainEntityAdapter reservationDateSettingDomainEntityAdapter;

    @InjectMocks
    ReservationDateSettingServiceImpl reservationDateSettingServiceImpl;

    @Mock
    ReservationService reservationService;

    @BeforeEach
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        reservationDateSettingServiceImpl = new ReservationDateSettingServiceImpl(reservationDateSettingDomainEntityAdapter, reservationService, objectMapper);
    }

    @Test
    void save_expectSuccess() {
        ReservationDateSettingDomain reservationDateSettingDomain = newReservationDateSettingDomain();


        reservationDateSettingDomainEntityAdapter.save(reservationDateSettingDomain);

        verify(reservationDateSettingDomainEntityAdapter, times(1)).save(reservationDateSettingDomain);
    }

    @Test
    void getById_expectSuccess() {

        ReservationDateSettingDomain expected = newReservationDateSettingDomain();
        Long id = 1L;
        when(reservationDateSettingDomainEntityAdapter
                .getById(id)).thenReturn(Optional.of(expected));

        ReservationDateSettingDomain actual = reservationDateSettingServiceImpl
                .getById(id);

        assertEquals(expected, actual);
    }

    @Test
    public void testUpdateReservation() {
        ReservationDateSettingDomain reservationDateSettingDomain = newReservationDateSettingDomain();

        ReservationDomain reservationDomain = newReservationDomain();

        when(reservationService.getById(1L)).thenReturn(reservationDomain);

        reservationDateSettingServiceImpl.updateReservation(reservationDateSettingDomain, 1L);

        assertEquals(reservationDateSettingDomain.getDateRange(), reservationDomain.getDateRange());
    }

    @Test
    void changeStatusOfRequest_IfStatusIsPendingAndRequestIsConfirmed_expectSuccess() {

        Long id = 1L;
        ReservationDateSettingDomain reservationDateSettingDomain = newReservationDateSettingDomain();

        when(reservationDateSettingDomainEntityAdapter.getById(id)).thenReturn(Optional.of(reservationDateSettingDomain));

        ReservationDomain reservationDomain = newReservationDomain();
        when(reservationService.getById(id)).thenReturn(reservationDomain);

        reservationDateSettingServiceImpl.changeStatusOfRequest(id, RequestStatus.CONFIRMED);

        verify(reservationService).save(reservationDomain);
    }


    ReservationDateSettingDomain newReservationDateSettingDomain() {
        return ReservationDateSettingDomain.builder()
                .id(1L)
                .dateRange(new DateRange(LocalDate.now(), LocalDate.now().plusDays(3)))
                .message("Can I change those dates?")
                .status(RequestStatus.PENDING)
                .build();
    }

    ReservationDomain newReservationDomain() {

        return ReservationDomain
                .builder()
                .creationDate(LocalDate.now())
                .status(ReservationStatus.ACTIVE)
                .totalAmount(BigDecimal.ONE)
                .dateRange(new DateRange(LocalDate.now(), LocalDate.now().plusDays(1)))
                .numberOfPeople(1)
                .accommodationUnitId(1L)
                .profileId(1L)
                .build();
    }

}
