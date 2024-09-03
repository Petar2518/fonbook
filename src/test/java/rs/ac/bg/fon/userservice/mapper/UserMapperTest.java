package rs.ac.bg.fon.userservice.mapper;

import rs.ac.bg.fon.userservice.dto.UserDto;
import rs.ac.bg.fon.userservice.repository.entity.UserEntity;
import rs.ac.bg.fon.userservice.service.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
class UserMapperTest {

    UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void fromEntityToModel() {
        UserEntity userEntity = UserEntity.builder()
                .userId(1L)
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("000")
                .dateOfBirth(LocalDate.now())
                .build();

        User user = mapper.fromEntityToModel(userEntity);

        assertNotNull(user);
        assertEquals(user.getUserId(), userEntity.getUserId());
        assertEquals(user.getFirstName(), userEntity.getFirstName());
        assertEquals(user.getLastName(), userEntity.getLastName());
        assertEquals(user.getPhoneNumber(), userEntity.getPhoneNumber());
        assertEquals(user.getDateOfBirth(), userEntity.getDateOfBirth());
    }

    @Test
    void fromModelToEntity() {
        User user = User.builder()
                .userId(1L)
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("000")
                .dateOfBirth(LocalDate.now())
                .build();

        UserEntity userEntity = mapper.fromModelToEntity(user);

        assertNotNull(userEntity);
        assertEquals(user.getUserId(), userEntity.getUserId());
        assertEquals(user.getFirstName(), userEntity.getFirstName());
        assertEquals(user.getLastName(), userEntity.getLastName());
        assertEquals(user.getPhoneNumber(), userEntity.getPhoneNumber());
        assertEquals(user.getDateOfBirth(), userEntity.getDateOfBirth());
    }


    @Test
    void fromModelToDto() {
        User user = User.builder()
                .userId(1L)
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("000")
                .dateOfBirth(LocalDate.now())
                .build();

        UserDto userDto = mapper.fromModelToDto(user);

        assertNotNull(userDto);
        assertEquals(user.getUserId(), userDto.getUserId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getPhoneNumber(), userDto.getPhoneNumber());
        assertEquals(user.getDateOfBirth(), userDto.getDateOfBirth());
    }

    @Test
    void fromDtoToModel() {
        UserDto userDto = UserDto.builder()
                .userId(1L)
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("000")
                .dateOfBirth(LocalDate.now())
                .build();

        User user = mapper.fromDtoToModel(userDto);

        assertNotNull(user);
        assertEquals(user.getUserId(), userDto.getUserId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getPhoneNumber(), userDto.getPhoneNumber());
        assertEquals(user.getDateOfBirth(), userDto.getDateOfBirth());
    }
}