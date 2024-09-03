package rs.ac.bg.fon.authenticationservice.exception.custom;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
