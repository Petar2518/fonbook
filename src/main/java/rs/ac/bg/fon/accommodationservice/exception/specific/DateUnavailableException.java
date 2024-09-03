package rs.ac.bg.fon.accommodationservice.exception.specific;

import org.springframework.http.HttpStatus;
import rs.ac.bg.fon.accommodationservice.exception.ApiException;

public class DateUnavailableException extends ApiException {
    public DateUnavailableException() {
        super("Price is already added for that date", HttpStatus.BAD_REQUEST);
    }
}
