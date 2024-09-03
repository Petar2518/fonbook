package rs.ac.bg.fon.authenticationservice.service.impl;

import rs.ac.bg.fon.authenticationservice.model.Account;
import rs.ac.bg.fon.authenticationservice.model.Role;
import rs.ac.bg.fon.authenticationservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountUserDetailsServiceImplTest {

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    AccountUserDetailsServiceImpl accountUserDetailsService;

    @Test
    void loadUserByUsername() {
        String email = "test@example.com";
        Account account = Account.builder()
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        when(accountRepository.getAccountByEmail(email)).thenReturn(Optional.ofNullable(account));

        UserDetails userDetails = accountUserDetailsService.loadUserByUsername(email);
        assertThat(userDetails).isNotNull();
        assertThat(email).isEqualTo(userDetails.getUsername());
        assertThat(account.getPassword()).isEqualTo(userDetails.getPassword());
        assertThat(1).isEqualTo(userDetails.getAuthorities().size());
    }

    @Test
    void loadUserByUsernameUserNotFound() {
        String email = "test@example.com";
        when(accountRepository.getAccountByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Account: " + email + " not found");
    }
}