package rs.ac.bg.fon.userservice.mapper;

import rs.ac.bg.fon.userservice.dto.UserDto;
import rs.ac.bg.fon.userservice.repository.entity.UserEntity;
import rs.ac.bg.fon.userservice.service.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity fromModelToEntity(User user);

    User fromEntityToModel(UserEntity userModel);

    UserDto fromModelToDto(User user);

    User fromDtoToModel(UserDto userDto);
}
