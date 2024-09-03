package rs.ac.bg.fon.authenticationservice.service;

import rs.ac.bg.fon.authenticationservice.dto.response.ForgotPasswordResponseDto;
import rs.ac.bg.fon.authenticationservice.model.Account;

import java.util.UUID;

public interface PasswordResetTokenService {

    ForgotPasswordResponseDto createPasswordResetToken(Account account);

    Long validateToken(UUID id);

    void deleteExpiredPasswordResetTokens();
}
