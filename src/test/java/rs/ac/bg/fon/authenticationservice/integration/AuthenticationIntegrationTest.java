package rs.ac.bg.fon.authenticationservice.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import rs.ac.bg.fon.authenticationservice.config.JwtConfig;
import rs.ac.bg.fon.authenticationservice.dto.AccountDto;
import rs.ac.bg.fon.authenticationservice.dto.request.AccountRegistrationRequestDto;
import rs.ac.bg.fon.authenticationservice.dto.request.AuthenticationRequestDto;
import rs.ac.bg.fon.authenticationservice.dto.response.AuthenticationResponseDto;
import rs.ac.bg.fon.authenticationservice.dto.response.RefreshTokenResponseDto;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@Tag("springboot")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationIntegrationTest extends AbstractTestContainers {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    JwtConfig jwtConfig;
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final String AUTH_URI = "/auth";
    private final String ACCOUNT_URI = "/accounts";

    @DynamicPropertySource
    private static void setExpirationTimes(DynamicPropertyRegistry registry) {
        registry.add("jwt.expiration-time", () -> 10);
        registry.add("jwt.refresh-token-expiration-time", () -> 600);
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM accounts");
    }

    @Test
    void refreshToken() throws InterruptedException, JsonProcessingException {
        String email = "test01@example.com";
        AccountRegistrationRequestDto request = new AccountRegistrationRequestDto(
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

        Long id = accountDto.getId();

        webTestClient.get()
                .uri(ACCOUNT_URI + "/verify-email/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        AuthenticationRequestDto authenticationRequest = new AuthenticationRequestDto(request.email(), request.password());

        AuthenticationResponseDto authenticationResponse = webTestClient.post()
                .uri(AUTH_URI + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequestDto.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponseDto>() {
                })
                .returnResult()
                .getResponseBody();

        String jwt = authenticationResponse.token();
        String refreshToken = authenticationResponse.refreshToken();

        Thread.sleep(10000);

        webTestClient.get()
                .uri(ACCOUNT_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtConfig.getPrefix() + jwt)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        RefreshTokenResponseDto responseBody = webTestClient.get()
                .uri(AUTH_URI + "/refresh-token")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtConfig.getPrefix() + refreshToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<RefreshTokenResponseDto>() {
                })
                .returnResult()
                .getResponseBody();

        String newJwt = responseBody.refreshToken();

        webTestClient.get()
                .uri(ACCOUNT_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtConfig.getPrefix() + newJwt)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void validateToken() {
        AccountRegistrationRequestDto request = new AccountRegistrationRequestDto(
                "test01@example.com",
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

        Long id = accountDto.getId();

        webTestClient.get()
                .uri(ACCOUNT_URI + "/verify-email/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        AuthenticationRequestDto authenticationRequest = new AuthenticationRequestDto("test01@example.com", "password");

        AuthenticationResponseDto authenticationResponse = webTestClient.post()
                .uri(AUTH_URI + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequestDto.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponseDto>() {
                })
                .returnResult()
                .getResponseBody();

        String jwt = authenticationResponse.token();

        webTestClient.get()
                .uri(AUTH_URI + "/validate-token")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtConfig.getPrefix() + jwt)
                .exchange()
                .expectStatus()
                .isOk();
    }
}
