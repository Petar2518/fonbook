package rs.ac.bg.fon.reservationservice.validators;

import rs.ac.bg.fon.reservationservice.constraints.validators.DateRangeValidator;
import rs.ac.bg.fon.reservationservice.dto.CreateReservationDateSettingDto;
import rs.ac.bg.fon.reservationservice.dto.CreateReservationDto;
import rs.ac.bg.fon.reservationservice.model.DateRange;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateRangeValidatorTest {


    @Test
    public void checkInIsBeforeToday_expectException() {
        DateRangeValidator dateRangeValidator = new DateRangeValidator();
        CreateReservationDto createReservationDto = newCreateReservationDto();
        createReservationDto.getDateRange().setCheckInDate(LocalDate.now().minusDays(1));

        CreateReservationDateSettingDto createReservationDateSettingDto = newCreateReservationDateSettingDto();
        createReservationDateSettingDto.getDateRange().setCheckInDate(LocalDate.now().minusDays(1));

        boolean validationWithCreateReservationDto = dateRangeValidator.isValid(createReservationDto.getDateRange(), null);
        boolean validationWithCreateModifyRequestDto = dateRangeValidator.isValid(createReservationDateSettingDto.getDateRange(), null);


        assertFalse(validationWithCreateReservationDto, "Check-in date shouldn't be in the past");
        assertFalse(validationWithCreateModifyRequestDto, "Check-in date shouldn't be in the past");
    }


    @Test
    public void checkInIsAfterCheckout_expectException() {
        DateRangeValidator dateRangeValidator = new DateRangeValidator();
        CreateReservationDto createReservationDto = newCreateReservationDto();
        createReservationDto.getDateRange().setCheckInDate(LocalDate.now().plusDays(6));

        CreateReservationDateSettingDto createReservationDateSettingDto = newCreateReservationDateSettingDto();
        createReservationDateSettingDto.getDateRange().setCheckInDate(LocalDate.now().plusDays(6));

        boolean validationWithCreateReservationDto = dateRangeValidator.isValid(createReservationDto.getDateRange(), null);
        boolean validationWithCreateModifyRequestDto = dateRangeValidator.isValid(createReservationDateSettingDto.getDateRange(), null);


        assertFalse(validationWithCreateReservationDto, "Check-in date shouldn't be after check-out");
        assertFalse(validationWithCreateModifyRequestDto, "Check-in date shouldn't be after check-out");
    }

    @Test
    public void checkInIsBeforeTodayAndBeforeCheckOut_expectSuccess() {
        DateRangeValidator dateRangeValidator = new DateRangeValidator();
        CreateReservationDto createReservationDto = newCreateReservationDto();

        CreateReservationDateSettingDto createReservationDateSettingDto = newCreateReservationDateSettingDto();

        boolean validationWithCreateReservationDto = dateRangeValidator.isValid(createReservationDto.getDateRange(), null);
        boolean validationWithCreateModifyRequestDto = dateRangeValidator.isValid(createReservationDateSettingDto.getDateRange(), null);


        assertTrue(validationWithCreateReservationDto, "Date range should be valid for CreateReservationDto");
        assertTrue(validationWithCreateModifyRequestDto, "Date range should be valid for CreateModifyRequestDto");
    }

    CreateReservationDateSettingDto newCreateReservationDateSettingDto() {
        return CreateReservationDateSettingDto
                .builder()
                .dateRange(new DateRange(LocalDate.now(), LocalDate.now().plusDays(5)))
                .build();
    }

    CreateReservationDto newCreateReservationDto() {
        return CreateReservationDto
                .builder()
                .dateRange(new DateRange(LocalDate.now(), LocalDate.now().plusDays(5)))
                .totalAmount(BigDecimal.ONE)
                .numberOfPeople(1)
                .accommodationUnitId(1L)
                .build();
    }

}
