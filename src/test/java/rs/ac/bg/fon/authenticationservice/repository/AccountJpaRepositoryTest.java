package rs.ac.bg.fon.authenticationservice.repository;

import rs.ac.bg.fon.authenticationservice.model.Role;
import rs.ac.bg.fon.authenticationservice.repository.entity.AccountEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("datajpa")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountJpaRepositoryTest {

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @Test
    @DisplayName("Account with email exists")
    void existsByEmail() {
        String email = "test@example.com";

        AccountEntity account = AccountEntity.builder()
                .email(email)
                .password("password")
                .valid(true)
                .role(Role.USER)
                .build();

        accountJpaRepository.save(account);

        boolean actual = accountJpaRepository.existsByEmail(email);

        assertThat(actual).isEqualTo(true);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("Duplicate email")
    void accountWithEmailAlreadyExist() {
        String email = "test@example.com";

        AccountEntity account1 = AccountEntity.builder()
                .email(email)
                .password("password")
                .valid(true)
                .role(Role.USER)
                .build();

        AccountEntity account2 = AccountEntity.builder()
                .email(email)
                .password("12345")
                .valid(true)
                .role(Role.HOST)
                .build();

        accountJpaRepository.save(account1);

        assertThatThrownBy(() -> accountJpaRepository.save(account2))
                .isInstanceOf(DataIntegrityViolationException.class);

        accountJpaRepository.delete(account1);
    }
}