package rs.ac.bg.fon.userservice.repository.impl;

import rs.ac.bg.fon.userservice.mapper.UserMapper;
import rs.ac.bg.fon.userservice.repository.UserJpaRepository;
import rs.ac.bg.fon.userservice.repository.entity.UserEntity;
import rs.ac.bg.fon.userservice.service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryImplTest {

    @Mock
    UserJpaRepository jpaRepository;
    @Mock
    UserMapper mapper;
    @InjectMocks
    UserRepositoryImpl repository;

    User user;
    UserEntity userEntity;

    @BeforeEach
    void setUp() {
        user = User.builder().userId(10L).firstName("first name").lastName("last name").build();
        userEntity = UserEntity.builder().userId(10L).firstName("first name").lastName("last name").build();
    }

    @Test
    void save() {
        when(mapper.fromModelToEntity(user)).thenReturn(userEntity);
        when(jpaRepository.save(userEntity)).thenReturn(userEntity);
        when(mapper.fromEntityToModel(userEntity)).thenReturn(user);

        User savedUser = repository.save(user);

        verify(jpaRepository).save(userEntity);
        assertNotNull(savedUser);
        assertEquals(savedUser, user);
    }

    @Test
    void deleteById() {
        repository.deleteById(1L);

        verify(jpaRepository).deleteById(1L);
    }

    @Test
    void findById() {
        when(jpaRepository.findById(10L)).thenReturn(Optional.of(userEntity));
        when(mapper.fromEntityToModel(userEntity)).thenReturn(user);

        Optional<User> foundUser = repository.findById(10L);

        verify(jpaRepository).findById(10L);
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(user);
    }

}
