package rs.ac.bg.fon.reservationservice.exceptions;

import rs.ac.bg.fon.reservationservice.exceptions.handler.HttpStatusException;
import org.springframework.http.HttpStatus;

public class ReservationAlreadyPaidException extends HttpStatusException {
    public ReservationAlreadyPaidException() {
        super("Reservation is already paid", HttpStatus.BAD_REQUEST);
    }
}
