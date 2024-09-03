package rs.ac.bg.fon.accommodationservice.exception.specific;

import org.springframework.http.HttpStatus;
import rs.ac.bg.fon.accommodationservice.exception.ApiException;

public class AddressNotFoundException extends ApiException {
    public AddressNotFoundException(Long id) {
        super("Address with id: " + id + " doesn't exist", HttpStatus.NOT_FOUND);
    }
}
