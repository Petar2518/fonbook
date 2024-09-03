package rs.ac.bg.fon.reservationservice.mapper;

import rs.ac.bg.fon.reservationservice.domain.ReservationDateSettingDomain;
import rs.ac.bg.fon.reservationservice.domain.ReservationDomain;
import rs.ac.bg.fon.reservationservice.dto.CreateReservationDateSettingDto;
import rs.ac.bg.fon.reservationservice.dto.ReservationDateSettingDto;
import rs.ac.bg.fon.reservationservice.model.DateRange;
import rs.ac.bg.fon.reservationservice.model.RequestStatus;
import rs.ac.bg.fon.reservationservice.model.ReservationDateSetting;
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
public class ModifyRequestMapperTest {

    @Autowired
    ReservationDateSettingMapper reservationDateSettingMapper;


    @Test
    public void mapCreateDtoToDomain_expectSuccess(){
        CreateReservationDateSettingDto createReservationDateSettingDto = newCreateReservationDateSettingDto();

        ReservationDateSettingDomain reservationDateSettingDomain = reservationDateSettingMapper.fromCreateDtoToDomain(createReservationDateSettingDto);

        assertEquals(reservationDateSettingDomain.getDateRange().getCheckInDate(), createReservationDateSettingDto.getDateRange().getCheckInDate());
        assertEquals(reservationDateSettingDomain.getDateRange().getCheckOutDate(), createReservationDateSettingDto.getDateRange().getCheckOutDate());
        assertEquals(reservationDateSettingDomain.getMessage(), createReservationDateSettingDto.getMessage());

    }

    @Test
    public void mapDomainToEntity_expectSuccess(){
        ReservationDomain reservationDomain = newReservationDomain();

        ReservationDateSettingDomain reservationDateSettingDomain = newReservationDateSettingDomain(reservationDomain);

         ReservationDateSetting reservationDateSetting = reservationDateSettingMapper.fromDomainToEntity(reservationDateSettingDomain);

        assertEquals(reservationDateSetting.getUpdatedCheckIn(), reservationDateSettingDomain.getDateRange().getCheckInDate());
        assertEquals(reservationDateSetting.getUpdatedCheckOut(), reservationDateSettingDomain.getDateRange().getCheckOutDate());
        assertEquals(reservationDateSetting.getMessage(), reservationDateSettingDomain.getMessage());
        assertEquals(reservationDateSetting.getStatus(), reservationDateSettingDomain.getStatus());
        assertEquals(reservationDateSetting.getId(), reservationDateSettingDomain.getId());


    }

    @Test
    public void mapDomainToDto_expectSuccess(){
        ReservationDomain reservationDomain = newReservationDomain();

        ReservationDateSettingDomain reservationDateSettingDomain = newReservationDateSettingDomain(reservationDomain);

        ReservationDateSettingDto reservationDateSettingDto = reservationDateSettingMapper.fromDomainToDto(reservationDateSettingDomain);

        assertEquals(reservationDateSettingDto.getDateRange().getCheckInDate(), reservationDateSettingDomain.getDateRange().getCheckInDate());
        assertEquals(reservationDateSettingDto.getDateRange().getCheckOutDate(), reservationDateSettingDomain.getDateRange().getCheckOutDate());
        assertEquals(reservationDateSettingDto.getMessage(), reservationDateSettingDomain.getMessage());
        assertEquals(reservationDateSettingDto.getStatus(), reservationDateSettingDomain.getStatus());
        assertEquals(reservationDateSettingDto.getId(), reservationDateSettingDomain.getId());


    }


    CreateReservationDateSettingDto newCreateReservationDateSettingDto(){
        return CreateReservationDateSettingDto
                .builder()
                .dateRange(new DateRange(LocalDate.now(), LocalDate.now().plusDays(5)))
                .build();
    }

    ReservationDateSettingDomain newReservationDateSettingDomain(ReservationDomain reservationDomain){
        return ReservationDateSettingDomain
                .builder()
                .id(reservationDomain.getId())
                .dateRange(new DateRange(LocalDate.now(), LocalDate.now().plusDays(5)))
                .status(RequestStatus.PENDING)
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



}
