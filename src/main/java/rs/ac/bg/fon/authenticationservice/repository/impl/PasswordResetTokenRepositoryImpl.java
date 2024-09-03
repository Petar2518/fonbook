package rs.ac.bg.fon.authenticationservice.repository.impl;

import rs.ac.bg.fon.authenticationservice.mapper.PasswordResetTokenMapper;
import rs.ac.bg.fon.authenticationservice.model.PasswordResetToken;
import rs.ac.bg.fon.authenticationservice.repository.PasswordResetTokenJpaRepository;
import rs.ac.bg.fon.authenticationservice.repository.PasswordResetTokenRepository;
import rs.ac.bg.fon.authenticationservice.repository.entity.PasswordResetTokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository {

    private final PasswordResetTokenJpaRepository passwordResetTokenRepository;
    private final PasswordResetTokenMapper passwordResetTokenMapper;

    @Override
    public void createPasswordResetToken(PasswordResetToken passwordResetToken) {
        passwordResetTokenRepository.save(passwordResetTokenMapper.modelToEntity(passwordResetToken));
    }

    @Override
    public Optional<PasswordResetToken> getPasswordResetTokenById(UUID id) {
        Optional<PasswordResetTokenEntity> passwordResetTokenEntity = passwordResetTokenRepository.findById(id);
        return passwordResetTokenEntity.map(passwordResetTokenMapper::entityToModel);
    }

    @Override
    public void deleteExpiredPasswordResetTokens(LocalDateTime now) {
        passwordResetTokenRepository.deleteByExpirationDateTimeBefore(now);
    }

    @Override
    public boolean isPasswordResetTokenAlreadySentToAccount(Long accountId) {
        return passwordResetTokenRepository.existsByAccountAccountId(accountId);
    }
}
