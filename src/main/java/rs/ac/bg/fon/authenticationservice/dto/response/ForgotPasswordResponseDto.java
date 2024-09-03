package rs.ac.bg.fon.authenticationservice.dto.response;

import java.util.UUID;

public record ForgotPasswordResponseDto(String email, UUID id) {
}
