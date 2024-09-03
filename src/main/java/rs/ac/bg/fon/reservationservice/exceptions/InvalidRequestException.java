package rs.ac.bg.fon.reservationservice.exceptions;

import rs.ac.bg.fon.reservationservice.exceptions.handler.HttpStatusException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Getter
@Setter
public class InvalidRequestException extends HttpStatusException {

    public InvalidRequestException() {
        super("Status can only be Confirmed or Cancelled", HttpStatus.BAD_REQUEST);
    }
}

