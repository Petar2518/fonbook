package rs.ac.bg.fon.authenticationservice.repository.impl;

import rs.ac.bg.fon.authenticationservice.mapper.PasswordResetTokenMapper;
import rs.ac.bg.fon.authenticationservice.model.Account;
import rs.ac.bg.fon.authenticationservice.model.PasswordResetToken;
import rs.ac.bg.fon.authenticationservice.repository.PasswordResetTokenJpaRepository;
import rs.ac.bg.fon.authenticationservice.repository.entity.AccountEntity;
import rs.ac.bg.fon.authenticationservice.repository.entity.PasswordResetTokenEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenRepositoryImplTest {

    @Mock
    PasswordResetTokenJpaRepository passwordResetTokenJpaRepository;

    @Mock
    PasswordResetTokenMapper passwordResetTokenMapper;

    @InjectMocks
    PasswordResetTokenRepositoryImpl passwordResetTokenRepository;

    PasswordResetToken passwordResetToken;
    PasswordResetTokenEntity passwordResetTokenEntity;

    @BeforeEach
    void setUp() {
        passwordResetTokenEntity =
                PasswordResetTokenEntity.builder()
                        .id(UUID.randomUUID())
                        .account(new AccountEntity())
                        .expirationDateTime(LocalDateTime.now().plusHours(1))
                        .build();

        passwordResetToken =
                PasswordResetToken.builder()
                        .id(UUID.randomUUID())
                        .account(new Account())
                        .expirationDateTime(LocalDateTime.now().plusHours(1))
                        .build();
    }

    @Test
    void createPasswordResetToken() {
        PasswordResetTokenEntity savePasswordResetTokenEntity = PasswordResetTokenEntity.builder()
                .id(UUID.randomUUID())
                .account(new AccountEntity())
                .expirationDateTime(LocalDateTime.now().plusHours(1))
                .build();

        when(passwordResetTokenMapper.modelToEntity(passwordResetToken)).thenReturn(passwordResetTokenEntity);
        when(passwordResetTokenJpaRepository.save(passwordResetTokenEntity)).thenReturn(savePasswordResetTokenEntity);

        passwordResetTokenRepository.createPasswordResetToken(passwordResetToken);

        verify(passwordResetTokenJpaRepository, times(1)).save(passwordResetTokenEntity);
    }

    @Test
    void getPasswordResetTokenById() {
        UUID id = UUID.randomUUID();
        when(passwordResetTokenJpaRepository.findById(id)).thenReturn(Optional.of(passwordResetTokenEntity));
        when(passwordResetTokenMapper.entityToModel(passwordResetTokenEntity)).thenReturn(passwordResetToken);

        Optional<PasswordResetToken> result = passwordResetTokenRepository.getPasswordResetTokenById(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(passwordResetToken);
    }

    @Test
    void deleteExpiredPasswordResetTokens() {
        doNothing().when(passwordResetTokenJpaRepository).deleteByExpirationDateTimeBefore(any(LocalDateTime.class));

        passwordResetTokenRepository.deleteExpiredPasswordResetTokens(LocalDateTime.now());

        verify(passwordResetTokenJpaRepository, times(1)).deleteByExpirationDateTimeBefore(any(LocalDateTime.class));
    }

    @Test
    void isPasswordResetTokenAlreadySentToAccount() {
        Long accountId = 1L;
        when(passwordResetTokenJpaRepository.existsByAccountAccountId(accountId)).thenReturn(true);

        boolean result = passwordResetTokenRepository.isPasswordResetTokenAlreadySentToAccount(accountId);

        assertThat(result).isTrue();
    }
}