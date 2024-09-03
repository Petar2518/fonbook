package rs.ac.bg.fon.authenticationservice.dto.request;

import rs.ac.bg.fon.authenticationservice.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AccountRegistrationRequestDto(
        @Email(message = "Invalid email address")
        String email,
        @Size(min = 5, message = "Password must contain at least 5 characters")
        String password,
        Role role) {
}
