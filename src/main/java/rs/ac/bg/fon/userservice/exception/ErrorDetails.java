package rs.ac.bg.fon.userservice.exception;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ErrorDetails {
    LocalDateTime time;
    String message;
    String details;
}
