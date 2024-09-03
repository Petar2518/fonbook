package rs.ac.bg.fon.authenticationservice.exception.custom;

public class PasswordResetTokenExpiredException extends RuntimeException {

    public PasswordResetTokenExpiredException(String message) {
        super(message);
    }
}
