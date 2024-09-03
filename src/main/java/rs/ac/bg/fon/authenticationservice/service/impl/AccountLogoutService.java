package rs.ac.bg.fon.authenticationservice.service.impl;

import rs.ac.bg.fon.authenticationservice.exception.custom.AccountNotFoundException;
import rs.ac.bg.fon.authenticationservice.model.Account;
import rs.ac.bg.fon.authenticationservice.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountLogoutService implements LogoutHandler {

    private final AccountRepository accountRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        String subject = authentication.getName();

        Account account = accountRepository.getAccountByEmail(subject).orElseThrow(() -> new AccountNotFoundException(
                String.format("Account with email %s doesn't exist!", subject)
        ));

        account.setTokenRevokedLastAt(LocalDateTime.now());
        accountRepository.updateAccount(account);
    }
}
