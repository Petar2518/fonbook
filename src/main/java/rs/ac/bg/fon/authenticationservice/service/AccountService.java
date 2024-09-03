package rs.ac.bg.fon.authenticationservice.service;

import rs.ac.bg.fon.authenticationservice.dto.AccountDto;
import rs.ac.bg.fon.authenticationservice.dto.request.AccountRegistrationRequestDto;
import rs.ac.bg.fon.authenticationservice.dto.request.ForgotPasswordRequestDto;
import rs.ac.bg.fon.authenticationservice.dto.request.ResetPasswordRequestDto;
import rs.ac.bg.fon.authenticationservice.dto.request.PasswordUpdateRequestDto;
import rs.ac.bg.fon.authenticationservice.dto.response.ForgotPasswordResponseDto;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountDto createAccount(AccountRegistrationRequestDto accountRegistrationRequest);

    AccountDto getAccountById(Long accountId);

    List<AccountDto> getAllAccounts();

    void deleteAccountById(Long accountId);

    void updatePassword(PasswordUpdateRequestDto passwordUpdateRequestDto);

    void verifyEmail(Long accountId);

    ForgotPasswordResponseDto forgotPassword(ForgotPasswordRequestDto forgotPasswordRequest);

    void resetPassword(UUID id, ResetPasswordRequestDto resetPasswordRequest);

    void activateAccountById(Long accountId);
}
