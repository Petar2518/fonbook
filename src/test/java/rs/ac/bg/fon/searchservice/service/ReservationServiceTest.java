package rs.ac.bg.fon.searchservice.service;

import rs.ac.bg.fon.searchservice.domain.ReservationDomain;
import rs.ac.bg.fon.searchservice.mapper.ReservationMapper;
import rs.ac.bg.fon.searchservice.model.Reservation;
import rs.ac.bg.fon.searchservice.model.ReservationStatus;
import rs.ac.bg.fon.searchservice.repository.ReservationRepository;
import rs.ac.bg.fon.searchservice.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    ReservationRepository reservationRepository;
    @Mock
    ReservationMapper reservationMapper;
    @InjectMocks
    ReservationServiceImpl reservationService;

    @Test
    public void saveAccommodation() {
        Reservation reservation = Reservation.builder()
                .id(1L)
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now())
                .reservationStatus(ReservationStatus.ACTIVE)
                .build();
        ReservationDomain reservationDomain = ReservationDomain.builder()
                .id(1L)
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now())
                .reservationStatus(ReservationStatus.ACTIVE)
                .build();

        when(reservationMapper.domainToEntity(reservationDomain)).thenReturn(reservation);

        reservationService.save(reservationDomain);

        verify(reservationRepository).save(reservation);
    }
}
