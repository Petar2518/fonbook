package rs.ac.bg.fon.authenticationservice.service.impl;

import rs.ac.bg.fon.authenticationservice.dto.AccountDto;
import rs.ac.bg.fon.authenticationservice.dto.request.AccountRegistrationRequestDto;
import rs.ac.bg.fon.authenticationservice.dto.request.PasswordUpdateRequestDto;
import rs.ac.bg.fon.authenticationservice.exception.custom.AccountNotFoundException;
import rs.ac.bg.fon.authenticationservice.exception.custom.DuplicateResourceException;
import rs.ac.bg.fon.authenticationservice.mapper.AccountMapper;
import rs.ac.bg.fon.authenticationservice.model.Account;
import rs.ac.bg.fon.authenticationservice.model.Role;
import rs.ac.bg.fon.authenticationservice.repository.AccountRepository;
import rs.ac.bg.fon.authenticationservice.repository.entity.AccountEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AccountMapper accountMapper;
    @InjectMocks
    AccountServiceImpl accountService;


    @Test
    void createAccountWhenEmailIsTaken() {
        String email = "test@example.com";
        AccountRegistrationRequestDto registrationRequest = new AccountRegistrationRequestDto(email, "password", Role.USER);

        Account account = Account.builder()
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        when(accountRepository.existsAccountWithEmail(email)).thenReturn(true);
        when(accountMapper.accountRegistrationRequestToAccount(registrationRequest)).thenReturn(account);

        assertThatThrownBy(() -> accountService.createAccount(registrationRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(
                        String.format("Email %s is already taken!", registrationRequest.email())
                );

        verify(accountRepository, never()).insertAccount(any());
    }

    @Test
    void createAccountSuccess() {
        String email = "test@example.com";

        AccountRegistrationRequestDto registrationRequest = new AccountRegistrationRequestDto(email, "password", Role.USER);

        Account account = Account.builder()
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        String passwordHash = "$2a$10$G7Q.P3QzV7CObA3D7hKTIOA6LNNtvGVSd1ylNFmm/CxbxZqih3DUW";

        Account savedAccount = Account.builder()
                .accountId(1L)
                .email(email)
                .password(passwordHash)
                .role(Role.USER)
                .build();

        when(accountRepository.existsAccountWithEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(account.getPassword())).thenReturn(passwordHash);
        when(accountMapper.accountRegistrationRequestToAccount(registrationRequest)).thenReturn(account);
        when(accountRepository.insertAccount(account)).thenReturn(savedAccount);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        accountService.createAccount(registrationRequest);

        verify(accountRepository, times(1)).insertAccount(captor.capture());

        Account capturedAccount = captor.getValue();
        assertThat(capturedAccount).isEqualTo(account);
    }

    @Test
    void getAccountById() {
        Long accountId = 1L;

        Account account = Account.builder()
                .accountId(accountId)
                .email("test@example.com")
                .valid(true)
                .role(Role.USER)
                .build();

        AccountDto accountDto = AccountDto.builder()
                .id(accountId)
                .email("test@example.com")
                .valid(true)
                .role(Role.USER)
                .build();

        when(accountRepository.getAccountById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.accountToAccountDto(account)).thenReturn(accountDto);

        AccountDto actual = accountService.getAccountById(accountId);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(accountId);
    }

    @Test
    void getAccountByIdAccountNotFound() {
        Long accountId = 1L;

        assertThatThrownBy(() -> accountService.getAccountById(accountId))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage(String.format("Account with id %s not found", accountId));
    }

    @Test
    void deleteAccountById() {
        Long id = 1L;

        when(accountRepository.existsAccountWithId(id)).thenReturn(true);

        accountService.deleteAccountById(id);

        verify(accountRepository).deleteAccountById(id);
    }

    @Test
    void deleteAccountByIdThrowsWhenIdNotExists() {
        Long id = 1L;

        when(accountRepository.existsAccountWithId(id)).thenReturn(false);

        assertThatThrownBy(() -> accountService.deleteAccountById(id))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage(
                        "Account with id %s not found".formatted(id)
                );

        verify(accountRepository, never()).deleteAccountById(id);
    }

    @Test
    void updatePasswordSuccess() {
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto(
                1L,
                "password",
                "newPassword"
        );

        String oldPasswordHash = "$2a$10$G7Q.P3QzV7CObA3D7hKTIOA6LNNtvGVSd1ylNFmm/CxbxZqih3DUW";
        String newPasswordHash = "$2a$10$SOMEOTHERHASH";

        Account account = Account.builder()
                .accountId(passwordUpdateRequestDto.accountId())
                .email("test@example.com")
                .password(oldPasswordHash)
                .role(Role.USER)
                .build();

        when(accountRepository.getAccountById(passwordUpdateRequestDto.accountId())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(passwordUpdateRequestDto.oldPassword(), account.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(passwordUpdateRequestDto.newPassword())).thenReturn(newPasswordHash);

        accountService.updatePassword(passwordUpdateRequestDto);

        assertThat(account.getPassword()).isEqualTo(newPasswordHash);
        verify(accountRepository, times(1)).updateAccount(account);
    }

    @Test
    void updatePasswordOldPasswordNotMatching() {
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto(
                1L,
                "password",
                "newPassword"
        );


        AccountEntity accountEntity = new AccountEntity();

        when(accountRepository.getAccountById(passwordUpdateRequestDto.accountId())).thenReturn(Optional.of(new Account()));
        when(passwordEncoder.matches(passwordUpdateRequestDto.oldPassword(), accountEntity.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> accountService.updatePassword(passwordUpdateRequestDto))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Old password is not matching!");

        verify(accountRepository, never()).insertAccount(any(Account.class));
    }

    @Test
    void updatePasswordEntityNotFoundException() {
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto(1L, "oldPassword", "newPassword");
        when(accountRepository.getAccountById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.updatePassword(passwordUpdateRequestDto))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account with id 1 doesn't exist!");
    }

}