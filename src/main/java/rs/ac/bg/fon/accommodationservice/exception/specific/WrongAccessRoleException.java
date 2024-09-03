package rs.ac.bg.fon.accommodationservice.exception.specific;

import org.springframework.http.HttpStatus;
import rs.ac.bg.fon.accommodationservice.exception.ApiException;

public class WrongAccessRoleException extends ApiException {
    public WrongAccessRoleException(String role) {
        super("User with role " + role + " doesn't have permission to delete accommodations.", HttpStatus.BAD_REQUEST);
    }
}
