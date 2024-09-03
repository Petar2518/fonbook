package rs.ac.bg.fon.reservationservice.exceptions;

import rs.ac.bg.fon.reservationservice.exceptions.handler.HttpStatusException;
import org.springframework.http.HttpStatus;

public class RequestAlreadyResolvedException extends HttpStatusException {
    public RequestAlreadyResolvedException() {
        super("You cannot change the current request because it has already been changed with status ", HttpStatus.BAD_REQUEST);
    }
}
