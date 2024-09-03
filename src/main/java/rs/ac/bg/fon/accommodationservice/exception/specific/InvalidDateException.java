package rs.ac.bg.fon.accommodationservice.exception.specific;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import rs.ac.bg.fon.accommodationservice.exception.ApiException;

@Slf4j
public class InvalidDateException extends ApiException {
    public InvalidDateException() {
        super("Date From field needs to be before Date To field", HttpStatus.BAD_REQUEST);
        log.error("Exception occurred when user tried to set date from after date to." );
    }
}
