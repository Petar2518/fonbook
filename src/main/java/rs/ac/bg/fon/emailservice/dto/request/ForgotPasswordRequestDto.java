package rs.ac.bg.fon.emailservice.dto.request;

import java.util.UUID;

public record ForgotPasswordRequestDto(String email, UUID id) {
}
