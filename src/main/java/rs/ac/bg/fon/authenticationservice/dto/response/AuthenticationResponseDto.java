package rs.ac.bg.fon.authenticationservice.dto.response;

import rs.ac.bg.fon.authenticationservice.dto.AccountDto;

public record AuthenticationResponseDto(
        String token,
        String refreshToken,
        AccountDto accountDto
) {
}
