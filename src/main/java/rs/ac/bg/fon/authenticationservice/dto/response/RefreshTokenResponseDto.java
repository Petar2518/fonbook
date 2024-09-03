package rs.ac.bg.fon.authenticationservice.dto.response;

public record RefreshTokenResponseDto(
        String accessToken,
        String refreshToken
) {
}
