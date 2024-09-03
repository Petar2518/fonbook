package rs.ac.bg.fon.authenticationservice.service.impl;

import rs.ac.bg.fon.authenticationservice.dto.response.ForgotPasswordResponseDto;
import rs.ac.bg.fon.authenticationservice.exception.custom.DuplicateResourceException;
import rs.ac.bg.fon.authenticationservice.exception.custom.PasswordResetTokenExpiredException;
import rs.ac.bg.fon.authenticationservice.exception.custom.PasswordResetTokenNotFoundException;
import rs.ac.bg.fon.authenticationservice.model.Account;
import rs.ac.bg.fon.authenticationservice.model.PasswordResetToken;
import rs.ac.bg.fon.authenticationservice.repository.PasswordResetTokenRepository;
import rs.ac.bg.fon.authenticationservice.service.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${password-reset-token.expiration-time}")
    private Long expirationTime;

    @Override
    public ForgotPasswordResponseDto createPasswordResetToken(Account account) {
        if (passwordResetTokenRepository.isPasswordResetTokenAlreadySentToAccount(account.getAccountId())) {
            throw new DuplicateResourceException(
                    String.format("Password reset token is already sent to email %s", account.getEmail())
            );
        }
        PasswordResetToken passwordResetToken =
                PasswordResetToken.builder()
                        .id(UUID.randomUUID())
                        .account(account)
                        .expirationDateTime(LocalDateTime.now().plusMinutes(expirationTime))
                        .build();

        passwordResetTokenRepository.createPasswordResetToken(passwordResetToken);
        log.info("Password reset token is created");

        return new ForgotPasswordResponseDto(passwordResetToken.getAccount().getEmail(), passwordResetToken.getId());
    }

    @Override
    public Long validateToken(UUID id) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.getPasswordResetTokenById(id)
                .orElseThrow(() -> new PasswordResetTokenNotFoundException(
                        String.format("Password reset token with id %s not found", id)
                ));

        if (passwordResetToken.getExpirationDateTime().isBefore(LocalDateTime.now())) {
            throw new PasswordResetTokenExpiredException("Password reset token has expired!");
        }

        return passwordResetToken.getAccount().getAccountId();
    }


    @Override
    @Transactional
    @Scheduled(fixedRateString = "${scheduler.fixed-rate}", timeUnit = TimeUnit.HOURS)
    public void deleteExpiredPasswordResetTokens() {
        passwordResetTokenRepository.deleteExpiredPasswordResetTokens(LocalDateTime.now());
    }
}
