package rs.ac.bg.fon.reservationservice.exceptions;

import rs.ac.bg.fon.reservationservice.exceptions.handler.HttpStatusException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
@Setter
public class ReservationAlreadyExistsException extends HttpStatusException {
    private LocalDate checkIn;
    private LocalDate checkOut;

    public ReservationAlreadyExistsException(LocalDate checkIn, LocalDate checkOut) {
        super(String.format("Apartment is booked within dates (%s - %s)", checkIn, checkOut), HttpStatus.BAD_REQUEST);
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }
}

