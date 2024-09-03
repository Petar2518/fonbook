package rs.ac.bg.fon.reservationservice.exceptions.handler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public abstract class HttpStatusException extends RuntimeException {

    private HttpStatus httpStatus;
    public HttpStatusException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus=httpStatus;
    }

}


