package rs.ac.bg.fon.reservationservice.mapper;

import rs.ac.bg.fon.reservationservice.domain.ReservationDomain;
import rs.ac.bg.fon.reservationservice.dto.CreateReservationDto;
import rs.ac.bg.fon.reservationservice.dto.ReservationDto;
import rs.ac.bg.fon.reservationservice.dto.message.ReservationEmailMessage;
import rs.ac.bg.fon.reservationservice.model.DateRange;
import rs.ac.bg.fon.reservationservice.model.Reservation;
import rs.ac.bg.fon.reservationservice.model.ReservationStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Tag("springboot")
public class ReservationMapperTest {

    @Autowired
    ReservationMapper reservationMapper;

    @Test
    public void mapReservationDomainToEntity_expectSuccess() {
        ReservationDomain reservationDomain = newReservationDomain();

        Reservation reservation = reservationMapper.fromDomainToEntity(reservationDomain);

        assertEquals(reservationDomain.getTotalAmount(), reservation.getTotalAmount());
        assertEquals(reservationDomain.getDateRange().getCheckInDate(), reservation.getCheckInDate());
        assertEquals(reservationDomain.getDateRange().getCheckOutDate(), reservation.getCheckOutDate());
        assertEquals(reservationDomain.getNumberOfPeople(), reservation.getNumberOfPeople());
        assertEquals(reservationDomain.getId(), reservation.getId());
        assertEquals(reservationDomain.getCreationDate(), reservation.getCreationDate());
        assertEquals(reservationDomain.getStatus(), reservation.getStatus());
        assertEquals(reservationDomain.getProfileId(), reservation.getProfileId());

    }


    @Test
    public void mapReservationEntityToDomain_expectSuccess() {
        Reservation reservation = newReservation();

        ReservationDomain reservationDomain = reservationMapper.fromEntityToDomain(reservation);

        assertEquals(reservationDomain.getTotalAmount(), reservation.getTotalAmount());
        assertEquals(reservationDomain.getDateRange().getCheckInDate(), reservation.getCheckInDate());
        assertEquals(reservationDomain.getDateRange().getCheckOutDate(), reservation.getCheckOutDate());
        assertEquals(reservationDomain.getNumberOfPeople(), reservation.getNumberOfPeople());
        assertEquals(reservationDomain.getId(), reservation.getId());
        assertEquals(reservationDomain.getCreationDate(), reservation.getCreationDate());
        assertEquals(reservationDomain.getStatus(), reservation.getStatus());
        assertEquals(reservationDomain.getProfileId(), reservation.getProfileId());

    }

    @Test
    public void mapCreateReservationDtoToDomain_expectSuccess() {
        CreateReservationDto createReservationDto = newCreateReservationDto();

        ReservationDomain reservationDomain = reservationMapper.fromCreateReservationDtoToDomain(createReservationDto, 1L);

        assertEquals(reservationDomain.getTotalAmount(), createReservationDto.getTotalAmount());
        assertEquals(reservationDomain.getDateRange().getCheckInDate(), createReservationDto.getDateRange().getCheckInDate());
        assertEquals(reservationDomain.getDateRange().getCheckOutDate(), createReservationDto.getDateRange().getCheckOutDate());
        assertEquals(reservationDomain.getNumberOfPeople(), createReservationDto.getNumberOfPeople());
        assertEquals(reservationDomain.getProfileId(), 1L);

    }

    @Test
    public void mapCreateReservationDomainToReservationDto_expectSuccess() {
        ReservationDomain reservationDomain = newReservationDomain();

        ReservationDto reservationDto = reservationMapper.fromDomainToReservationDto(reservationDomain);

        assertEquals(reservationDomain.getTotalAmount(), reservationDto.getTotalAmount());
        assertEquals(reservationDomain.getDateRange().getCheckInDate(), reservationDto.getDateRange().getCheckInDate());
        assertEquals(reservationDomain.getDateRange().getCheckOutDate(), reservationDto.getDateRange().getCheckOutDate());
        assertEquals(reservationDomain.getNumberOfPeople(), reservationDto.getNumberOfPeople());
        assertEquals(reservationDomain.getId(), reservationDto.getId());
        assertEquals(reservationDomain.getCreationDate(), reservationDto.getCreationDate());
        assertEquals(reservationDomain.getStatus(), reservationDto.getStatus());
        assertEquals(reservationDomain.getProfileId(), reservationDto.getProfileId());
    }

    @Test
    public void mapReservationToReservationEmailMessage_expectSuccess() {
        Reservation reservation = newReservation();
        String email = "test@example.com";

        ReservationEmailMessage reservationEmailMessage = reservationMapper.fromEntityToReservationEmailMessage(reservation, email);

        assertEquals(email, reservationEmailMessage.getEmail());
        assertEquals(reservation.getCheckInDate(), reservationEmailMessage.getCheckInDate());
        assertEquals(reservation.getCheckOutDate(), reservationEmailMessage.getCheckOutDate());
        assertEquals(reservation.getTotalAmount(), reservationEmailMessage.getTotalAmount());
        assertEquals(reservation.getCurrency(), reservationEmailMessage.getCurrency());
        assertEquals(reservation.getNumberOfPeople(), reservationEmailMessage.getNumberOfPeople());
    }


    CreateReservationDto newCreateReservationDto() {
        return CreateReservationDto
                .builder()
                .dateRange(new DateRange(LocalDate.now().plusDays(4), LocalDate.now().plusDays(11)))
                .totalAmount(BigDecimal.ONE)
                .numberOfPeople(1)
                .accommodationUnitId(1L)
                .build();
    }

    ReservationDomain newReservationDomain() {
        return ReservationDomain
                .builder()
                .id(0L)
                .creationDate(LocalDate.now())
                .status(ReservationStatus.ACTIVE)
                .dateRange(new DateRange(LocalDate.now().plusDays(4), LocalDate.now().plusDays(11)))
                .totalAmount(BigDecimal.ONE)
                .numberOfPeople(1)
                .accommodationUnitId(1L)
                .profileId(1L)
                .build();
    }

    Reservation newReservation() {
        return Reservation
                .builder()
                .creationDate(LocalDate.now())
                .status(ReservationStatus.ACTIVE)
                .checkOutDate(LocalDate.of(2022, 1, 1))
                .totalAmount(BigDecimal.ONE)
                .currency("USD")
                .checkInDate(LocalDate.of(2022, 2, 2))
                .numberOfPeople(1)
                .accommodationUnitId(1L)
                .profileId(1L)
                .build();
    }


}
