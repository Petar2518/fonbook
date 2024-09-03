package rs.ac.bg.fon.reservationservice.exceptions;

import rs.ac.bg.fon.reservationservice.exceptions.handler.HttpStatusException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@EqualsAndHashCode(callSuper = true)
@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
@Setter

public class MapperException extends HttpStatusException {

    private String message;

    public MapperException(String message) {
        super("Error while updating \n" +  message, HttpStatus.BAD_REQUEST);
    }
}
