package rs.ac.bg.fon.authenticationservice.service.impl;

import rs.ac.bg.fon.authenticationservice.config.RabbitMQConfig;
import rs.ac.bg.fon.authenticationservice.dto.AccountDto;
import rs.ac.bg.fon.authenticationservice.dto.request.*;
import rs.ac.bg.fon.authenticationservice.dto.response.ForgotPasswordResponseDto;
import rs.ac.bg.fon.authenticationservice.exception.custom.AccountIsAlreadyActivatedException;
import rs.ac.bg.fon.authenticationservice.exception.custom.AccountNotFoundException;
import rs.ac.bg.fon.authenticationservice.exception.custom.DuplicateResourceException;
import rs.ac.bg.fon.authenticationservice.mapper.AccountMapper;
import rs.ac.bg.fon.authenticationservice.model.Account;
import rs.ac.bg.fon.authenticationservice.rabbitmq.RabbitMQMessageProducer;
import rs.ac.bg.fon.authenticationservice.repository.AccountRepository;
import rs.ac.bg.fon.authenticationservice.service.AccountService;
import rs.ac.bg.fon.authenticationservice.service.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenService passwordResetTokenService;
    private final RabbitMQMessageProducer producer;
    private final RabbitMQConfig rabbitMQConfig;

    @Override
    public AccountDto createAccount(AccountRegistrationRequestDto accountRegistrationRequest) {
        Account account = accountMapper.accountRegistrationRequestToAccount(accountRegistrationRequest);

        if (accountRepository.existsAccountWithEmail(account.getEmail())) {
            throw new DuplicateResourceException(
                    String.format("Email %s is already taken!", accountRegistrationRequest.email())
            );
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setActive(false);
        Account savedAccount = accountRepository.insertAccount(account);
        return accountMapper.accountToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long accountId) {
        Account account = accountRepository.getAccountById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format("Account with id %s not found", accountId)
                ));

        return accountMapper.accountToAccountDto(account);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        return accountMapper.accountsToAccountDtos(accountRepository.getAllAccounts());
    }

    @Override
    public void deleteAccountById(Long accountId) {
        if (!accountRepository.existsAccountWithId(accountId)) {
            throw new AccountNotFoundException(String.format("Account with id %s not found", accountId));
        }

        accountRepository.deleteAccountById(accountId);
    }

    @Override
    public void updatePassword(PasswordUpdateRequestDto passwordUpdateRequestDto) {
        Account account = accountRepository.getAccountById(passwordUpdateRequestDto.accountId())
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format("Account with id %s doesn't exist!", passwordUpdateRequestDto.accountId())
                ));

        if (!passwordEncoder.matches(passwordUpdateRequestDto.oldPassword(), account.getPassword())) {
            throw new BadCredentialsException("Old password is not matching!");
        }

        account.setPassword(passwordEncoder.encode(passwordUpdateRequestDto.newPassword()));
        account.setTokenRevokedLastAt(LocalDateTime.now());
        accountRepository.updateAccount(account);
    }

    @Override
    public void verifyEmail(Long accountId) {
        Account account = accountRepository.getAccountById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format("Account with id %s doesn't exist!", accountId)
                ));

        if (account.isValid()) {
            throw new AccountIsAlreadyActivatedException(String.format("Account with id %s is already activated", accountId));
        }

        account.setValid(true);
        accountRepository.updateAccount(account);
    }

    @Override
    public ForgotPasswordResponseDto forgotPassword(ForgotPasswordRequestDto forgotPasswordRequest) {
        Account account = accountRepository.getAccountByEmail(forgotPasswordRequest.email())
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format("Account with email %s doesn't exist!", forgotPasswordRequest.email())
                ));

        return passwordResetTokenService.createPasswordResetToken(account);
    }

    @Override
    public void resetPassword(UUID id, ResetPasswordRequestDto resetPasswordRequest) {
        if (!resetPasswordRequest.newPassword().equals(resetPasswordRequest.confirmedNewPassword())) {
            throw new BadCredentialsException("Passwords don't match");
        }

        Long accountId = passwordResetTokenService.validateToken(id);
        log.info("Password reset token is validated");

        Account account = accountRepository.getAccountById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format("Account with id %s doesn't exist!", accountId)
                ));

        account.setPassword(passwordEncoder.encode(resetPasswordRequest.newPassword()));
        accountRepository.updateAccount(account);
    }

    @Override
    public void activateAccountById(Long accountId) {

        Account account = accountRepository.getAccountById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format("Account with id %s doesn't exist!", accountId)
                ));
        try {
            account.setActive(true);
            accountRepository.updateAccount(account);
            producer.publish(
                    new VerificationEmailRequestDto(account.getEmail(),
                            rabbitMQConfig.getVerificationEmailPath() + accountId),
                    rabbitMQConfig.getExchange(),
                    rabbitMQConfig.getVerificationRoutingKey());
            log.info("Verification email is sent to {}", rabbitMQConfig.getVerificationEmailPath());
        }catch (Exception e){
            throw new AccountNotFoundException(String.format("Account with id %s cannot be activated!", accountId));
        }
    }
}
