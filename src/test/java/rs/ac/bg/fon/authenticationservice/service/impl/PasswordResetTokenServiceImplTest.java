package rs.ac.bg.fon.authenticationservice.service.impl;

import rs.ac.bg.fon.authenticationservice.dto.response.ForgotPasswordResponseDto;
import rs.ac.bg.fon.authenticationservice.exception.custom.DuplicateResourceException;
import rs.ac.bg.fon.authenticationservice.exception.custom.PasswordResetTokenExpiredException;
import rs.ac.bg.fon.authenticationservice.exception.custom.PasswordResetTokenNotFoundException;
import rs.ac.bg.fon.authenticationservice.model.Account;
import rs.ac.bg.fon.authenticationservice.model.PasswordResetToken;
import rs.ac.bg.fon.authenticationservice.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceImplTest {

    @Mock
    PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    PasswordResetTokenServiceImpl passwordResetTokenService;

    private Account account;
    private UUID id;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .accountId(1L)
                .email("test@example.com")
                .build();

        id = UUID.randomUUID();

        ReflectionTestUtils.setField(passwordResetTokenService, "expirationTime", 3L);
    }

    @Test
    void createPasswordResetTokenWhenToken() {
        when(passwordResetTokenRepository.isPasswordResetTokenAlreadySentToAccount(account.getAccountId()))
                .thenReturn(false);

        ForgotPasswordResponseDto responseDto = passwordResetTokenService.createPasswordResetToken(account);

        assertThat(responseDto.id()).isNotNull();
        assertThat(responseDto.email()).isEqualTo(account.getEmail());

        verify(passwordResetTokenRepository).createPasswordResetToken(any());
    }

    @Test
    void createPasswordResetTokenWhenTokenAlreadySent() {
        when(passwordResetTokenRepository.isPasswordResetTokenAlreadySentToAccount(account.getAccountId()))
                .thenReturn(true);

        assertThatThrownBy(() -> passwordResetTokenService.createPasswordResetToken(account))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Password reset token is already sent to email " + account.getEmail());

        verify(passwordResetTokenRepository, never()).createPasswordResetToken(any());
    }


    @Test
    void validateTokenWhenTokenNotFound() {
        when(passwordResetTokenRepository.getPasswordResetTokenById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordResetTokenService.validateToken(id))
                .isInstanceOf(PasswordResetTokenNotFoundException.class)
                .hasMessageContaining("Password reset token with id " + id + " not found");
    }

    @Test
    void validateTokenWhenTokenExpired() {
        PasswordResetToken expiredToken = new PasswordResetToken();
        expiredToken.setExpirationDateTime(LocalDateTime.now().minusHours(1));
        expiredToken.setAccount(account);
        when(passwordResetTokenRepository.getPasswordResetTokenById(id))
                .thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> passwordResetTokenService.validateToken(id))
                .isInstanceOf(PasswordResetTokenExpiredException.class)
                .hasMessageContaining("Password reset token has expired!");
    }

    @Test
    void validateTokenWhenTokenValid() {

        PasswordResetToken validToken = new PasswordResetToken();
        validToken.setExpirationDateTime(LocalDateTime.now().plusHours(1));
        validToken.setAccount(account);
        when(passwordResetTokenRepository.getPasswordResetTokenById(id))
                .thenReturn(Optional.of(validToken));

        Long accountId = passwordResetTokenService.validateToken(id);

        assertThat(accountId).isEqualTo(account.getAccountId());
    }

    @Test
    void deleteExpiredPasswordResetTokens() {
        passwordResetTokenService.deleteExpiredPasswordResetTokens();

        verify(passwordResetTokenRepository).deleteExpiredPasswordResetTokens(any());
    }
}
