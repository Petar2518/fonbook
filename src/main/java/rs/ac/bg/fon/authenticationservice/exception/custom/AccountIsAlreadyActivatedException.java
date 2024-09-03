package rs.ac.bg.fon.authenticationservice.exception.custom;

public class AccountIsAlreadyActivatedException extends RuntimeException {

    public AccountIsAlreadyActivatedException(String message) {
        super(message);
    }
}
