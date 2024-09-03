package rs.ac.bg.fon.authenticationservice.dto.request;

import jakarta.validation.constraints.Email;

public record ForgotPasswordRequestDto(
        @Email(message = "Invalid email address")
        String email
) {
}
