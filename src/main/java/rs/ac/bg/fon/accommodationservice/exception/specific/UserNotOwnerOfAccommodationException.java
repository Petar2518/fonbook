package rs.ac.bg.fon.accommodationservice.exception.specific;

import org.springframework.http.HttpStatus;
import rs.ac.bg.fon.accommodationservice.exception.ApiException;

public class UserNotOwnerOfAccommodationException extends ApiException {
    public UserNotOwnerOfAccommodationException(Long hostId, Long accommodationId) {
        super("Host with id " + hostId + " is not assigned as owner of accommodation with id " + accommodationId, HttpStatus.BAD_REQUEST);
    }
}
