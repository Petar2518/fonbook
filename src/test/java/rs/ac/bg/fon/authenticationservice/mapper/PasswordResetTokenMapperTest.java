package rs.ac.bg.fon.authenticationservice.mapper;

import rs.ac.bg.fon.authenticationservice.model.Account;
import rs.ac.bg.fon.authenticationservice.model.PasswordResetToken;
import rs.ac.bg.fon.authenticationservice.repository.entity.AccountEntity;
import rs.ac.bg.fon.authenticationservice.repository.entity.PasswordResetTokenEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordResetTokenMapperTest {

    PasswordResetTokenMapper passwordResetTokenMapper = Mappers.getMapper(PasswordResetTokenMapper.class);

    @Test
    void accountEntityToAccount() {
        PasswordResetTokenEntity passwordResetTokenEntity = PasswordResetTokenEntity.builder()
                .id(UUID.randomUUID())
                .account(new AccountEntity())
                .expirationDateTime(LocalDateTime.now().plusHours(3L))
                .build();

        PasswordResetToken passwordResetToken = passwordResetTokenMapper.entityToModel(passwordResetTokenEntity);

        assertThat(passwordResetToken.getId()).isEqualTo(passwordResetTokenEntity.getId());
        assertThat(passwordResetToken.getAccount()).isNotNull();
        assertThat(passwordResetToken.getExpirationDateTime()).isEqualTo(passwordResetTokenEntity.getExpirationDateTime());

    }

    @Test
    void accountToAccountEntity() {
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .id(UUID.randomUUID())
                .account(new Account())
                .expirationDateTime(LocalDateTime.now().plusHours(3L))
                .build();


        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenMapper.modelToEntity(passwordResetToken);

        assertThat(passwordResetTokenEntity.getId()).isEqualTo(passwordResetToken.getId());
        assertThat(passwordResetTokenEntity.getAccount()).isNotNull();
        assertThat(passwordResetTokenEntity.getExpirationDateTime()).isEqualTo(passwordResetToken.getExpirationDateTime());
    }
}
