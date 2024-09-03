package rs.ac.bg.fon.hostservice.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<ErrorDetails> handleNotFoundException(HttpStatusException httpStatusException, WebRequest webRequest) {
        return createResponse(httpStatusException, webRequest);
    }

    public ResponseEntity<ErrorDetails> createResponse(HttpStatusException exception, WebRequest webRequest){

        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .details(webRequest.getDescription(false))
                .build();

        return new ResponseEntity<>(errorDetails, exception.getHttpStatus());
    }

}
