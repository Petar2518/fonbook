package rs.ac.bg.fon.authenticationservice.exception.custom;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
