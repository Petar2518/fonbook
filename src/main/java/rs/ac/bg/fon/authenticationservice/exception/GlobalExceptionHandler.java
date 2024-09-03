package rs.ac.bg.fon.authenticationservice.exception;

import rs.ac.bg.fon.authenticationservice.exception.custom.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ErrorDetails handleAllExceptions(Exception ex, WebRequest request) {
        return new ErrorDetails(ex, request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public final ErrorDetails handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) {
        return new ErrorDetails(ex, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ErrorDetails handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(errorMessage)
                .details(request.getDescription(false))
                .build();
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ErrorDetails handleAccountNotFoundException(AccountNotFoundException ex, WebRequest request) {
        return new ErrorDetails(ex, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public final ErrorDetails handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return new ErrorDetails(ex, request);
    }

    @ExceptionHandler(AccountIsAlreadyActivatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ErrorDetails handleAccountIsAlreadyActivatedException(AccountIsAlreadyActivatedException ex, WebRequest request) {
        return new ErrorDetails(ex, request);
    }

    @ExceptionHandler(CannotActivateAccountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ErrorDetails handleCannotActivateAccountException(CannotActivateAccountException ex, WebRequest request) {
        return new ErrorDetails(ex, request);
    }

    @ExceptionHandler(PasswordResetTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ErrorDetails handlePasswordResetTokenNotFoundException(PasswordResetTokenNotFoundException ex, WebRequest request) {
        return new ErrorDetails(ex, request);
    }

    @ExceptionHandler(PasswordResetTokenExpiredException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public final ErrorDetails handlePasswordResetTokenExpiredException(PasswordResetTokenExpiredException ex, WebRequest request) {
        return new ErrorDetails(ex, request);
    }
}
