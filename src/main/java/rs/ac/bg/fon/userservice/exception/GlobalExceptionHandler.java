package rs.ac.bg.fon.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception e, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .time(LocalDateTime.now())
                .message(e.getMessage())
                .details(request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorDetails> handleArgumentNotValidException(Exception e, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .time(LocalDateTime.now())
                .message(e.getMessage())
                .details(request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(ActivateUserException.class)
    public final ResponseEntity<ErrorDetails> handleActivateUserException(Exception e, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .time(LocalDateTime.now())
                .message(e.getMessage())
                .details(request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }


    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleUserNotFoundException(UserNotFoundException e, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .time(LocalDateTime.now())
                .message(e.getMessage())
                .details(request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(UserExistsException.class)
    public final ResponseEntity<ErrorDetails> handleUserExistsException(UserExistsException e, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .time(LocalDateTime.now())
                .message(e.getMessage())
                .details(request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

}
