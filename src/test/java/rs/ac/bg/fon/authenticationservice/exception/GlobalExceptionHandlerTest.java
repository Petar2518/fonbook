package rs.ac.bg.fon.authenticationservice.exception;

import rs.ac.bg.fon.authenticationservice.exception.custom.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    public void testHandleAllExceptions() {
        Exception ex = new Exception("Test exception message");
        ErrorDetails errorDetails = globalExceptionHandler.handleAllExceptions(ex, webRequest);

        assertThat(errorDetails.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(errorDetails.getMessage()).isEqualTo("Test exception message");
        assertThat(errorDetails.getDetails()).isEqualTo(webRequest.getDescription(false));
    }

    @Test
    public void testHandleDuplicateResourceException() {
        DuplicateResourceException ex = new DuplicateResourceException("Duplicate resource message");
        ErrorDetails errorDetails = globalExceptionHandler.handleDuplicateResourceException(ex, webRequest);

        assertThat(errorDetails.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(errorDetails.getMessage()).isEqualTo("Duplicate resource message");
        assertThat(errorDetails.getDetails()).isEqualTo(webRequest.getDescription(false));
    }

    @Test
    public void testHandleAccountNotFoundException() {
        AccountNotFoundException ex = new AccountNotFoundException("Account not found");
        ErrorDetails errorDetails = globalExceptionHandler.handleAccountNotFoundException(ex, webRequest);

        assertThat(errorDetails.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(errorDetails.getMessage()).isEqualTo("Account not found");
        assertThat(errorDetails.getDetails()).isEqualTo(webRequest.getDescription(false));
    }

    @Test
    public void testHandleBadCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");
        ErrorDetails errorDetails = globalExceptionHandler.handleBadCredentialsException(ex, webRequest);

        assertThat(errorDetails.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(errorDetails.getMessage()).isEqualTo("Bad credentials");
        assertThat(errorDetails.getDetails()).isEqualTo(webRequest.getDescription(false));
    }

    @Test
    public void testHandleAccountIsAlreadyActivated() {
        AccountIsAlreadyActivatedException ex = new AccountIsAlreadyActivatedException("Account is already activated");
        ErrorDetails errorDetails = globalExceptionHandler.handleAccountIsAlreadyActivatedException(ex, webRequest);

        assertThat(errorDetails.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(errorDetails.getMessage()).isEqualTo("Account is already activated");
        assertThat(errorDetails.getDetails()).isEqualTo(webRequest.getDescription(false));
    }

    @Test
    public void testHandlePasswordResetTokenNotFound() {
        PasswordResetTokenNotFoundException ex = new PasswordResetTokenNotFoundException("Password reset token not found");
        ErrorDetails errorDetails = globalExceptionHandler.handlePasswordResetTokenNotFoundException(ex, webRequest);

        assertThat(errorDetails.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(errorDetails.getMessage()).isEqualTo("Password reset token not found");
        assertThat(errorDetails.getDetails()).isEqualTo(webRequest.getDescription(false));
    }

    @Test
    public void testHandlePasswordResetTokenExpiredException() {
        PasswordResetTokenExpiredException ex = new PasswordResetTokenExpiredException("Password reset token has expired");
        ErrorDetails errorDetails = globalExceptionHandler.handlePasswordResetTokenExpiredException(ex, webRequest);

        assertThat(errorDetails.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(errorDetails.getMessage()).isEqualTo("Password reset token has expired");
        assertThat(errorDetails.getDetails()).isEqualTo(webRequest.getDescription(false));
    }
}