package rs.ac.bg.fon.authenticationservice.repository.impl;

import rs.ac.bg.fon.authenticationservice.mapper.AccountMapper;
import rs.ac.bg.fon.authenticationservice.model.Account;
import rs.ac.bg.fon.authenticationservice.model.Role;
import rs.ac.bg.fon.authenticationservice.repository.AccountJpaRepository;
import rs.ac.bg.fon.authenticationservice.repository.entity.AccountEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryImplTest {

    @Mock
    AccountJpaRepository accountJpaRepository;

    @Mock
    AccountMapper accountMapper;

    @InjectMocks
    AccountRepositoryImpl accountRepository;

    Account account;
    AccountEntity accountEntity;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .accountId(1L)
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        accountEntity = AccountEntity.builder()
                .accountId(1L)
                .email("test@example.com")
                .password("password")
                .valid(true)
                .role(Role.USER)
                .build();
    }

    @Test
    void getAllAccounts() {

        List<AccountEntity> accountEntities = Collections.singletonList(accountEntity);

        when(accountJpaRepository.findAll()).thenReturn(accountEntities);
        when(accountMapper.accountEntitiesToAccounts(accountEntities)).thenReturn(Collections.singletonList(account));
        List<Account> accounts = accountRepository.getAllAccounts();

        verify(accountJpaRepository, times(1)).findAll();
        assertThat(accounts).hasSize(1);
    }

    @Test
    void getAccountById() {
        Long id = 1L;

        when(accountJpaRepository.findById(id)).thenReturn(Optional.of(accountEntity));
        when(accountMapper.accountEntityToAccount(accountEntity)).thenReturn(account);

        Optional<Account> optionalAccount = accountRepository.getAccountById(id);

        verify(accountJpaRepository, times(1)).findById(id);
        assertThat(optionalAccount).isPresent();
    }

    @Test
    void insertAccount() {
        AccountEntity savedAccountEntity = AccountEntity.builder()
                .accountId(1L)
                .email("test@example.com")
                .password("password")
                .valid(true)
                .role(Role.USER)
                .build();

        Account insertedAcc = account;

        when(accountMapper.accountToAccountEntity(account)).thenReturn(accountEntity);
        when(accountJpaRepository.save(accountEntity)).thenReturn(savedAccountEntity);
        when(accountMapper.accountEntityToAccount(savedAccountEntity)).thenReturn(insertedAcc);

        Account result = accountRepository.insertAccount(account);

        verify(accountJpaRepository, times(1)).save(accountEntity);
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(insertedAcc.getEmail());
        assertThat(result.getAccountId()).isEqualTo(insertedAcc.getAccountId());
    }

    @Test
    void existsAccountWithEmail() {
        String email = account.getEmail();
        when(accountJpaRepository.existsByEmail(email)).thenReturn(true);

        boolean exists = accountRepository.existsAccountWithEmail(email);

        verify(accountJpaRepository, times(1)).existsByEmail(email);
        assertThat(exists).isTrue();
    }

    @Test
    void existsAccountWithId() {
        Long id = account.getAccountId();
        when(accountJpaRepository.existsById(id)).thenReturn(true);

        boolean exists = accountRepository.existsAccountWithId(id);

        verify(accountJpaRepository, times(1)).existsById(id);
        assertThat(exists).isTrue();
    }

    @Test
    void deleteAccountById() {
        Long id = account.getAccountId();

        accountRepository.deleteAccountById(id);

        verify(accountJpaRepository, times(1)).deleteById(id);
    }

    @Test
    void updatePassword() {
        when(accountMapper.accountToAccountEntity(account)).thenReturn(accountEntity);

        accountRepository.updateAccount(account);

        verify(accountJpaRepository, times(1)).save(accountEntity);
    }
}