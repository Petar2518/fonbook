package rs.ac.bg.fon.accommodationservice.exception.specific;

import org.springframework.http.HttpStatus;
import rs.ac.bg.fon.accommodationservice.exception.ApiException;

public class PriceNotFoundException extends ApiException {
    public PriceNotFoundException(Long id) {
        super("Price with id: " + id + " doesn't exist", HttpStatus.NOT_FOUND);
    }
}
