package rs.ac.bg.fon.authenticationservice.repository;

import rs.ac.bg.fon.authenticationservice.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    List<Account> getAllAccounts();

    Optional<Account> getAccountById(Long accountId);

    Optional<Account> getAccountByEmail(String email);

    Account insertAccount(Account account);

    boolean existsAccountWithEmail(String email);

    boolean existsAccountWithId(Long accountId);

    void deleteAccountById(Long accountId);

    void updateAccount(Account account);

}
