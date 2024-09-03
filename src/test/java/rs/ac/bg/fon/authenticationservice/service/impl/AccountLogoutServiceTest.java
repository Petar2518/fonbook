package rs.ac.bg.fon.authenticationservice.service.impl;

import rs.ac.bg.fon.authenticationservice.model.Account;
import rs.ac.bg.fon.authenticationservice.repository.AccountRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountLogoutServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AccountLogoutService accountLogoutService;

    @Test
    void logout() {
        String email = "test@example.com";
        Account account = Account.builder()
                .email(email)
                .build();

        Optional<Account> optionalAccount = Optional.of(account);

        when(authentication.getName()).thenReturn(email);
        when(accountRepository.getAccountByEmail(email)).thenReturn(optionalAccount);

        accountLogoutService.logout(null, null, authentication);

        verify(accountRepository, times(1)).updateAccount(account);
    }

    @Test
    void logoutNullAuthentication() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        accountLogoutService.logout(null, response, null);

        verify(response, times(1)).setStatus(403);
        verifyNoInteractions(accountRepository);
    }
}