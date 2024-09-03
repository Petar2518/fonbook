package rs.ac.bg.fon.authenticationservice.dto.request;

import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDto(
        @Size(min = 5, message = "Password must contain at least 5 characters")
        String newPassword,

        @Size(min = 5, message = "Password must contain at least 5 characters")
        String confirmedNewPassword
) {
}
