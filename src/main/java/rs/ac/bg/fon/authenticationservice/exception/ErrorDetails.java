package rs.ac.bg.fon.authenticationservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor
public class ErrorDetails {

    LocalDateTime timestamp;
    String message;
    String details;

    public ErrorDetails(Exception e, WebRequest request) {
        this.timestamp = LocalDateTime.now();
        this.message = e.getMessage();
        this.details = request.getDescription(false);
    }

}
