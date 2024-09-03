package rs.ac.bg.fon.accommodationservice.exception;

import lombok.Value;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Value
public class ErrorDetails {
    LocalDateTime errorTime;
    String message;
    String details;

    public ErrorDetails(Exception e, WebRequest request) {
        this.errorTime = LocalDateTime.now();
        this.message = e.getMessage();
        this.details = request.getDescription(false);
    }
}
