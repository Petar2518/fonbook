package rs.ac.bg.fon.reservationservice.exceptions;

import rs.ac.bg.fon.reservationservice.exceptions.handler.HttpStatusException;
import org.springframework.http.HttpStatus;

public class NotDefinedPricesForDatesException extends HttpStatusException {

    public NotDefinedPricesForDatesException() {
        super("No pricing information available for the selected dates.", HttpStatus.BAD_REQUEST);
    }
}
