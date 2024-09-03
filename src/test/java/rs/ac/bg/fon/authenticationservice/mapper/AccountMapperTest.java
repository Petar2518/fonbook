package rs.ac.bg.fon.authenticationservice.mapper;

import rs.ac.bg.fon.authenticationservice.dto.AccountDto;
import rs.ac.bg.fon.authenticationservice.dto.request.AccountRegistrationRequestDto;
import rs.ac.bg.fon.authenticationservice.model.Account;
import rs.ac.bg.fon.authenticationservice.model.Role;
import rs.ac.bg.fon.authenticationservice.repository.entity.AccountEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AccountMapperTest {

    AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Test
    void accountRegistrationRequestToAccount() {
        AccountRegistrationRequestDto request = new AccountRegistrationRequestDto(
                "text@example.com",
                "password",
                Role.USER);

        Account account = accountMapper.accountRegistrationRequestToAccount(request);

        assertThat(request.email()).isEqualTo(account.getEmail());
        assertThat(request.password()).isEqualTo(account.getPassword());
        assertThat(request.role()).isEqualTo(account.getRole());
    }

    @Test
    void accountEntityToAccount() {
        AccountEntity accountEntity = AccountEntity.builder()
                .accountId(1L)
                .email("test@example.com")
                .password("password")
                .valid(false)
                .role(Role.USER)
                .build();

        Account account = accountMapper.accountEntityToAccount(accountEntity);

        assertThat(accountEntity.getAccountId()).isEqualTo(account.getAccountId());
        assertThat(accountEntity.getEmail()).isEqualTo(account.getEmail());
        assertThat(accountEntity.getPassword()).isEqualTo(account.getPassword());
        assertThat(accountEntity.isValid()).isEqualTo(account.isValid());
        assertThat(accountEntity.getRole()).isEqualTo(account.getRole());
    }

    @Test
    void accountToAccountEntity() {
        Account account = Account.builder()
                .email("test@example.com")
                .password("password")
                .valid(false)
                .role(Role.USER)
                .build();

        AccountEntity accountEntity = accountMapper.accountToAccountEntity(account);

        assertThat(account.getEmail()).isEqualTo(accountEntity.getEmail());
        assertThat(account.getPassword()).isEqualTo(accountEntity.getPassword());
        assertThat(account.isValid()).isEqualTo(accountEntity.isValid());
        assertThat(account.getRole()).isEqualTo(accountEntity.getRole());
    }

    @Test
    void accountEntitiesToAccounts() {
        AccountEntity accountEntity1 = AccountEntity.builder()
                .accountId(1L)
                .email("test1@example.com")
                .password("password")
                .valid(false)
                .role(Role.USER)
                .build();

        AccountEntity accountEntity2 = AccountEntity.builder()
                .accountId(2L)
                .email("test2@example.com")
                .password("password")
                .valid(false)
                .role(Role.ADMIN)
                .build();

        List<AccountEntity> accountEntities = Arrays.asList(accountEntity1, accountEntity2);

        List<Account> accounts = accountMapper.accountEntitiesToAccounts(accountEntities);

        assertThat(accountEntity1.getAccountId()).isEqualTo(accounts.get(0).getAccountId());
        assertThat(accountEntity1.getEmail()).isEqualTo(accounts.get(0).getEmail());
        assertThat(accountEntity1.getPassword()).isEqualTo(accounts.get(0).getPassword());
        assertThat(accountEntity1.isValid()).isEqualTo(accounts.get(0).isValid());
        assertThat(accountEntity1.getRole()).isEqualTo(accounts.get(0).getRole());

        assertThat(accountEntity2.getAccountId()).isEqualTo(accounts.get(1).getAccountId());
        assertThat(accountEntity2.getEmail()).isEqualTo(accounts.get(1).getEmail());
        assertThat(accountEntity2.getPassword()).isEqualTo(accounts.get(1).getPassword());
        assertThat(accountEntity2.isValid()).isEqualTo(accounts.get(1).isValid());
        assertThat(accountEntity2.getRole()).isEqualTo(accounts.get(1).getRole());
    }

    @Test
    void accountsToAccountDtos() {
        Account account1 = Account.builder()
                .accountId(1L)
                .email("test1@example.com")
                .password("password")
                .valid(false)
                .role(Role.USER)
                .build();

        Account account2 = Account.builder()
                .accountId(2L)
                .email("test2@example.com")
                .password("password")
                .valid(false)
                .role(Role.ADMIN)
                .build();

        List<Account> accounts = Arrays.asList(account1, account2);

        List<AccountDto> accountDtos = accountMapper.accountsToAccountDtos(accounts);

        assertThat(accounts.size()).isEqualTo(accountDtos.size());

        assertThat(account1.getAccountId()).isEqualTo(accountDtos.get(0).getId());
        assertThat(account1.getEmail()).isEqualTo(accountDtos.get(0).getEmail());
        assertThat(account1.isActive()).isEqualTo(accountDtos.get(0).isValid());

        assertThat(account2.getAccountId()).isEqualTo(accountDtos.get(1).getId());
        assertThat(account2.getEmail()).isEqualTo(accountDtos.get(1).getEmail());
        assertThat(account2.isActive()).isEqualTo(accountDtos.get(1).isValid());
    }

    @Test
    void accountToAccountDto() {
        Account account = Account.builder()
                .accountId(1L)
                .email("test@example.com")
                .valid(false)
                .role(Role.USER)
                .build();

        AccountDto accountDto = accountMapper.accountToAccountDto(account);

        assertThat(account.getAccountId()).isEqualTo(accountDto.getId());
        assertThat(account.getEmail()).isEqualTo(accountDto.getEmail());
        assertThat(account.getRole()).isEqualTo(accountDto.getRole());
        assertThat(account.isActive()).isEqualTo(accountDto.isValid());
    }
}