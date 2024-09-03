package rs.ac.bg.fon.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ActivateUserException extends RuntimeException {
    public ActivateUserException(String message) {
        super(message);
    }
}
