package rs.ac.bg.fon.reservationservice.exceptions;

import rs.ac.bg.fon.reservationservice.exceptions.handler.HttpStatusException;
import org.springframework.http.HttpStatus;

public class ReservationNotLongerExistsException extends HttpStatusException {
    public ReservationNotLongerExistsException() {
        super("Reservation was already completed or canceled ", HttpStatus.BAD_REQUEST);
    }
}