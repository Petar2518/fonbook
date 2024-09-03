package rs.ac.bg.fon.authenticationservice.mapper;

import rs.ac.bg.fon.authenticationservice.dto.AccountDto;
import rs.ac.bg.fon.authenticationservice.dto.request.AccountRegistrationRequestDto;
import rs.ac.bg.fon.authenticationservice.model.Account;
import rs.ac.bg.fon.authenticationservice.repository.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "valid", constant = "false")
    @Mapping(target = "tokenRevokedLastAt", ignore = true)
    @Mapping(target = "active", constant = "false")
    Account accountRegistrationRequestToAccount(AccountRegistrationRequestDto registrationRequest);

    Account accountEntityToAccount(AccountEntity accountEntity);

    AccountEntity accountToAccountEntity(Account account);

    default List<Account> accountEntitiesToAccounts(List<AccountEntity> accountEntities) {
        return accountEntities.stream()
                .map(this::accountEntityToAccount)
                .collect(Collectors.toList());
    }

    default List<AccountDto> accountsToAccountDtos(List<Account> accounts) {
        return accounts.stream()
                .map(this::accountToAccountDto)
                .collect(Collectors.toList());
    }

    @Mapping(source = "accountId", target = "id")
    AccountDto accountToAccountDto(Account account);

}
