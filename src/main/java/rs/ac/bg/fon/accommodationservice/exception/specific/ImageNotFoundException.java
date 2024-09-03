package rs.ac.bg.fon.accommodationservice.exception.specific;

import org.springframework.http.HttpStatus;
import rs.ac.bg.fon.accommodationservice.exception.ApiException;

public class ImageNotFoundException extends ApiException {
    public ImageNotFoundException(Long id) {
        super("Image with id: " + id + " doesn't exist", HttpStatus.NOT_FOUND);
    }
}
