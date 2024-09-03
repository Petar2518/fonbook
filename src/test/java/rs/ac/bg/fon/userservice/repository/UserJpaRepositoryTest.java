package rs.ac.bg.fon.userservice.repository;

import rs.ac.bg.fon.userservice.repository.entity.UserEntity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Tag("datajpa")
class UserJpaRepositoryTest {

    @Autowired
    UserJpaRepository userJpaRepository;

    @Test
    void findById() {
        UserEntity savedUser = userJpaRepository.save(UserEntity.builder().userId(1L).firstName("name").lastName("lastName").build());

        UserEntity foundUser = userJpaRepository.findById(savedUser.getUserId()).get();

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserId()).isEqualTo(savedUser.getUserId());
        assertThat(foundUser.getFirstName()).isEqualTo("name");
        assertThat(foundUser.getLastName()).isEqualTo("lastName");
    }

    @Test
    void findAllById() {
        UserEntity savedUser = userJpaRepository.save(UserEntity.builder().userId(2L).firstName("name").lastName("lastName").build());

        List<UserEntity> foundUsers = userJpaRepository.findAllById(List.of(savedUser.getUserId()));

        assertThat(foundUsers.size()).isEqualTo(1);
        assertThat(foundUsers.get(0).getUserId()).isEqualTo(savedUser.getUserId());
        assertThat(foundUsers.get(0).getFirstName()).isEqualTo("name");
        assertThat(foundUsers.get(0).getLastName()).isEqualTo("lastName");
    }

}