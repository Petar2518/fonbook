package rs.ac.bg.fon.authenticationservice.exception.custom;

public class PasswordResetTokenNotFoundException extends RuntimeException {

    public PasswordResetTokenNotFoundException(String message) {
        super(message);
    }
}
