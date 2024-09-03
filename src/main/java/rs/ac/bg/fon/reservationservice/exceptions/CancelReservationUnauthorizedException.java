package rs.ac.bg.fon.reservationservice.exceptions;

import rs.ac.bg.fon.reservationservice.exceptions.handler.HttpStatusException;
import org.springframework.http.HttpStatus;

public class CancelReservationUnauthorizedException extends HttpStatusException {
    public CancelReservationUnauthorizedException() {
        super("Your id doesn't match user id in reservation", HttpStatus.UNAUTHORIZED);
    }
}
