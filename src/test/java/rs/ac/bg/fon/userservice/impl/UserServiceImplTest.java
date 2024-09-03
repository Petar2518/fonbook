package rs.ac.bg.fon.userservice.impl;

import rs.ac.bg.fon.userservice.dto.UserDto;
import rs.ac.bg.fon.userservice.exception.UserNotFoundException;
import rs.ac.bg.fon.userservice.mapper.UserMapper;
import rs.ac.bg.fon.userservice.repository.UserRepository;
import rs.ac.bg.fon.userservice.repository.entity.UserEntity;
import rs.ac.bg.fon.userservice.service.impl.UserServiceImpl;
import rs.ac.bg.fon.userservice.service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserMapper mapper;
    @Mock
    UserRepository repository;
    @InjectMocks
    UserServiceImpl service;

    UserEntity userEntity;
    UserDto userDto;
    User user;

    @BeforeEach
    void setUp() {
        user = User.builder().userId(1L).firstName("firstName").lastName("lastName").build();
        userDto = UserDto.builder().userId(1L).firstName("firstName").lastName("lastName").build();
        userEntity = UserEntity.builder().userId(1L).firstName("firstName").lastName("lastName").build();
        lenient().when(repository.save(user)).thenReturn(user);
        lenient().when(repository.findById(1L)).thenReturn(Optional.ofNullable(user));
        lenient().when(mapper.fromModelToEntity(user)).thenReturn(userEntity);
        lenient().when(mapper.fromEntityToModel(userEntity)).thenReturn(user);
        lenient().when(mapper.fromModelToDto(user)).thenReturn(userDto);
        lenient().when(mapper.fromDtoToModel(userDto)).thenReturn(user);
    }

    @Test
    void createUserCallsRepositorySave() {
        lenient().when(repository.findById(1L)).thenReturn(Optional.empty());

        UserDto createdUser = service.createUser(userDto);

        verify(repository).save(user);
        assertNotNull(createdUser);
        assertEquals(createdUser, userDto);
    }

    @Test
    void getUserNotFound() {
        when(repository.findById(-1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getUser(-1L));
    }

    @Test
    void getUserFound() {
        UserDto foundUser = service.getUser(1L);

        assertNotNull(foundUser);
        assertEquals(foundUser, userDto);
    }

    @Test
    void updateOperationWhenUserNotFound() {
        when(repository.findById(-1L)).thenReturn(Optional.empty());
        when(mapper.fromDtoToModel(UserDto.builder().userId(-1L).build())).thenReturn(User.builder().userId(-1L).build());

        assertThrows(UserNotFoundException.class, () -> service.updateUser(UserDto.builder().userId(-1L).build()));
    }

    @Test
    void deleteUser() {
        service.deleteUser(1L);

        verify(repository).deleteById(1L);
    }
}