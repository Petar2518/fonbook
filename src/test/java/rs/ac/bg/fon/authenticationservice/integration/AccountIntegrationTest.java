package rs.ac.bg.fon.authenticationservice.integration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import rs.ac.bg.fon.authenticationservice.config.JwtConfig;
import rs.ac.bg.fon.authenticationservice.dto.AccountDto;
import rs.ac.bg.fon.authenticationservice.dto.request.*;
import rs.ac.bg.fon.authenticationservice.dto.response.AuthenticationResponseDto;
import rs.ac.bg.fon.authenticationservice.dto.response.ForgotPasswordResponseDto;
import rs.ac.bg.fon.authenticationservice.model.Role;
import rs.ac.bg.fon.authenticationservice.testcontainers.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("springboot")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AccountIntegrationTest extends AbstractTestContainers {
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    JwtConfig jwtConfig;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final String ACCOUNT_URI = "/accounts";
    private final String AUTH_URI = "/auth";

    private String email;
    private AccountRegistrationRequestDto request;
    private Long id;
    private String jwt;
    private String xsrfToken;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM accounts");

        email = "test01@example.com";
        request = new AccountRegistrationRequestDto(
                email,
                "password",
                Role.USER);

        AccountDto accountDto = webTestClient.post()
                .uri(ACCOUNT_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AccountRegistrationRequestDto.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(new ParameterizedTypeReference<AccountDto>() {
                })
                .returnResult()
                .getResponseBody();

        id = accountDto.getId();


        webTestClient.get()
                .uri(ACCOUNT_URI + "/verify-email/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        AuthenticationRequestDto authenticationRequest = new AuthenticationRequestDto(request.email(), request.password());

        EntityExchangeResult<AuthenticationResponseDto> authenticationResponse = webTestClient.post()
                .uri(AUTH_URI + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequestDto.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AuthenticationResponseDto.class)
                .returnResult();

        jwt = authenticationResponse.getResponseBody().token();
        xsrfToken = authenticationResponse.getResponseHeaders().get("X-XSRF-TOKEN").toString();
    }


    @Test
    void registerAccount() {
        AccountDto expectedAccountDto = new AccountDto(id, "test01@example.com", true, Role.USER);

        webTestClient.get()
                .uri(ACCOUNT_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtConfig.getPrefix() + jwt)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AccountDto>() {
                })
                .isEqualTo(expectedAccountDto);
    }


    @Test
    void deleteAccountById() {
        webTestClient.delete()
                .uri(ACCOUNT_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .header(HttpHeaders.AUTHORIZATION, jwtConfig.getPrefix() + jwt)
                .header("X-XSRF-TOKEN", xsrfToken)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void updatePassword() throws InterruptedException {
        PasswordUpdateRequestDto updateRequest = new PasswordUpdateRequestDto(id, "password", "newPassword");

        webTestClient.put()
                .uri(ACCOUNT_URI + "/update-password")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .header(HttpHeaders.AUTHORIZATION, jwtConfig.getPrefix() + jwt)
                .header("X-XSRF-TOKEN", xsrfToken)
                .body(Mono.just(updateRequest), PasswordUpdateRequestDto.class)
                .exchange()
                .expectStatus()
                .isOk();

        AuthenticationRequestDto authRequest = new AuthenticationRequestDto(request.email(), updateRequest.newPassword());

        Thread.sleep(10000);


        EntityExchangeResult<AuthenticationResponseDto> authResponse = webTestClient.post()
                .uri(AUTH_URI + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authRequest), AuthenticationRequestDto.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AuthenticationResponseDto.class)
                .returnResult();

        String newJwt = authResponse.getResponseBody().token();

        AccountDto actual = webTestClient.get()
                .uri(ACCOUNT_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtConfig.getPrefix() + newJwt)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AccountDto.class)
                .returnResult()
                .getResponseBody();

        AccountDto expected = AccountDto.builder()
                .id(id)
                .email(email)
                .valid(true)
                .role(Role.USER)
                .build();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllAccounts() {
        AccountRegistrationRequestDto request2 = new AccountRegistrationRequestDto(
                "test02@example.com",
                "password",
                Role.USER);

        AccountDto account2 = webTestClient.post()
                .uri(ACCOUNT_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), AccountRegistrationRequestDto.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(new ParameterizedTypeReference<AccountDto>() {
                })
                .returnResult()
                .getResponseBody();

        Long id2 = account2.getId();

        webTestClient.get()
                .uri(ACCOUNT_URI + "/verify-email/{id}", id2)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        List<AccountDto> allAccounts = webTestClient.get()
                .uri(ACCOUNT_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtConfig.getPrefix() + jwt)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<AccountDto>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(allAccounts.size()).isEqualTo(2);

        AccountDto firstAccount = allAccounts.get(0);
        assertThat(firstAccount.getEmail()).isEqualTo(request.email());
        assertThat(firstAccount.getRole()).isEqualTo(request.role());

        AccountDto secondAccount = allAccounts.get(1);
        assertThat(secondAccount.getEmail()).isEqualTo(request2.email());
        assertThat(secondAccount.getRole()).isEqualTo(request2.role());
    }

    @Test
    void emailAlreadyExist() {
        webTestClient.post()
                .uri(ACCOUNT_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AccountRegistrationRequestDto.class)
                .exchange()
                .expectBody()
                .jsonPath("$.message").isEqualTo(String.format("Email %s is already taken!", request.email()));
    }

    @Test
    void accountWithIdNotExist() {
        webTestClient.get()
                .uri(ACCOUNT_URI + "/999")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtConfig.getPrefix() + jwt)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void oldPasswordNotMatching() {
        PasswordUpdateRequestDto updateRequest = new PasswordUpdateRequestDto(id, "notmatching", "newPassword");

        webTestClient.put()
                .uri(ACCOUNT_URI + "/update-password")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtConfig.getPrefix() + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), PasswordUpdateRequestDto.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }


    @Test
    void forgotPasswordAndReset() {
        ForgotPasswordRequestDto forgotPasswordRequest = new ForgotPasswordRequestDto(email);
        ForgotPasswordResponseDto forgotPasswordResponse = webTestClient.post()
                .uri(ACCOUNT_URI + "/forgot-password")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(forgotPasswordRequest), ForgotPasswordRequestDto.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ForgotPasswordResponseDto>() {
                })
                .returnResult()
                .getResponseBody();


        ResetPasswordRequestDto resetPasswordRequest = new ResetPasswordRequestDto("newPassword", "newPassword");
        webTestClient.put()
                .uri(ACCOUNT_URI + "/reset-password/" + forgotPasswordResponse.id())
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(resetPasswordRequest), ResetPasswordRequestDto.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void logout() {
        webTestClient.post()
                .uri(AUTH_URI + "/logout")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtConfig.getPrefix() + jwt)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri(ACCOUNT_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtConfig.getPrefix() + jwt)
                .exchange()
                .expectStatus()
                .isForbidden();
    }
}
