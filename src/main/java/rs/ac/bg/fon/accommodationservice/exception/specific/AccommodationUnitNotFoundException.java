package rs.ac.bg.fon.accommodationservice.exception.specific;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import rs.ac.bg.fon.accommodationservice.exception.ApiException;

@Getter
public class AccommodationUnitNotFoundException extends ApiException {
    public AccommodationUnitNotFoundException(Long id) {
        super("Accommodation unit with id: " + id + " doesn't exist", HttpStatus.NOT_FOUND);
    }
}
