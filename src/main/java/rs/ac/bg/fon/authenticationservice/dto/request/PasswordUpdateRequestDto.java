package rs.ac.bg.fon.authenticationservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PasswordUpdateRequestDto(
        @NotNull
        Long accountId,
        @Size(min = 5, message = "Password must contain at least 5 characters")
        String oldPassword,
        @Size(min = 5, message = "Password must contain at least 5 characters")
        String newPassword
) {
}
