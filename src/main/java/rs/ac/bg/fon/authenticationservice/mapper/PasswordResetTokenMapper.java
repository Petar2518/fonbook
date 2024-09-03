package rs.ac.bg.fon.authenticationservice.mapper;

import rs.ac.bg.fon.authenticationservice.model.PasswordResetToken;
import rs.ac.bg.fon.authenticationservice.repository.entity.PasswordResetTokenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasswordResetTokenMapper {

    PasswordResetTokenEntity modelToEntity(PasswordResetToken passwordResetToken);

    PasswordResetToken entityToModel(PasswordResetTokenEntity passwordResetTokenEntity);
}
