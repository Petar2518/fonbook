package rs.ac.bg.fon.accommodationservice.exception.specific;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import rs.ac.bg.fon.accommodationservice.exception.ApiException;

@Getter
public class AccommodationNotFoundException extends ApiException {

    public AccommodationNotFoundException(Long id) {
        super("Accommodation with id: " + id + " doesn't exist", HttpStatus.NOT_FOUND);
    }

}
