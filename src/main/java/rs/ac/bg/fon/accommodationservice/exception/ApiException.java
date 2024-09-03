package rs.ac.bg.fon.accommodationservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException {

    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public ApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
    public ApiException(String message, HttpStatus httpStatus,Exception e) {
        super(message,e);
        this.httpStatus = httpStatus;
    }
}
