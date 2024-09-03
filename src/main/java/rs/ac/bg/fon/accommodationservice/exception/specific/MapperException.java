package rs.ac.bg.fon.accommodationservice.exception.specific;

import org.springframework.http.HttpStatus;
import rs.ac.bg.fon.accommodationservice.exception.ApiException;

public class MapperException extends ApiException {
    public MapperException(Exception e) {
        super("Error while updating", HttpStatus.BAD_REQUEST,e);
    }
}
