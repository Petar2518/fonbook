package rs.ac.bg.fon.reservationservice.constraints.validators;

import rs.ac.bg.fon.reservationservice.constraints.DateRangeConstraint;
import rs.ac.bg.fon.reservationservice.model.DateRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<DateRangeConstraint, DateRange> {

    @Override
    public void initialize(DateRangeConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(DateRange dateRange, ConstraintValidatorContext constraintValidatorContext) {

        return validateCheckInAndCheckOutDates(dateRange.getCheckInDate(), dateRange.getCheckOutDate());
    }

    public boolean validateCheckInAndCheckOutDates(LocalDate checkIn, LocalDate checkOut) {
        LocalDate now = LocalDate.now();
        return !checkIn.isBefore(now) && checkIn.isBefore(checkOut);
    }

}


