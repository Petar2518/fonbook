package rs.ac.bg.fon.bookinggateway.integration;

import rs.ac.bg.fon.bookinggateway.config.RequestHeadersConfig;
import rs.ac.bg.fon.bookinggateway.dto.RegistrationDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Tag("springboot")
public class LogoutTest {

    @Autowired
    private RequestHeadersConfig headersConfig;
    @Autowired
    private WebTestClient webTestClient;
    private static RegistrationDto userRegistrationDetails;
    private static String userLoginDetails;
    private static RegistrationDto hostRegistrationDetails;
    private static String hostLoginDetails;
    private static String userJwt;
    private static String hostJwt;
    private static String userId;
    private static String hostId;
    private static String userXsrf;
    private static String hostXsrf;

    @BeforeEach
    public void setup() {
        userRegistrationDetails = RegistrationDto.builder()
                .email("useremail@gmail.com")
                .password("password")
                .role("USER")
                .firstName("name")
                .lastName("last name")
                .build();
        userLoginDetails = """
                {
                    "email": "useremail@gmail.com",
                    "password": "password"
                }
                """;
        hostRegistrationDetails = RegistrationDto.builder()
                .email("hostemail@gmail.com")
                .password("password")
                .role("HOST")
                .name("name")
                .phoneNumber("0")
                .bankAccountNumber("000000000")
                .build();
        hostLoginDetails = """
                {
                    "email": "hostemail@gmail.com",
                    "password": "password"
                }
                """;

        registerUser();
        loginUser();

        registerHost();
        loginHost();
    }


    @Test
    void logoutUser() {
        webTestClient.post()
                .uri("/logout")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .header(headersConfig.getXsrfCookieHeader(), userXsrf)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/users/" + userId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .header(headersConfig.getXsrfCookieHeader(), userXsrf)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void logoutHost() {
        webTestClient.post()
                .uri("/logout")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .header(headersConfig.getXsrfCookieHeader(), hostXsrf)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/hosts/" + hostId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .header(headersConfig.getXsrfCookieHeader(), hostXsrf)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @AfterEach
    void afterEach() throws InterruptedException {

        Thread.sleep(1000);

        loginUser();
        loginHost();

        webTestClient.delete()
                .uri("/users/" + userId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .header(headersConfig.getXsrfCookieHeader(), userXsrf)
                .exchange()
                .expectStatus().isOk();

        webTestClient.delete()
                .uri("/hosts/" + hostId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .header(headersConfig.getXsrfCookieHeader(), hostXsrf)
                .exchange()
                .expectStatus().isOk();
    }


    private void registerUser() {
        WebTestClient.ResponseSpec registerResponse = webTestClient.post()
                .uri("/register")
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRegistrationDetails)
                .exchange()
                .expectStatus().isCreated();

        String registerResponseBody = registerResponse.returnResult(String.class).getResponseBody().blockFirst();
        assertNotNull(registerResponseBody);
        userId = Arrays.stream(registerResponseBody.split("\"")).toList().get(2).substring(1).split(",")[0].trim();

        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://localhost:8083/accounts/verify-email/" + userId);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setDoOutput(true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader ignored = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loginUser() {
        WebTestClient.ResponseSpec loginResponse = webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userLoginDetails)
                .exchange()
                .expectStatus().isOk();

        loginResponse.expectHeader().exists(headersConfig.getXsrfCookieHeader());

        String responseBody = loginResponse.returnResult(String.class).getResponseBody().blockFirst();
        assertNotNull(responseBody);
        userJwt = Arrays.stream(responseBody.split("\"")).toList().get(3);
        userXsrf = Objects.requireNonNull(loginResponse.returnResult(String.class)
                        .getResponseHeaders()
                        .get(headersConfig.getXsrfCookieHeader()))
                .getFirst();
    }

    private void registerHost() {
        WebTestClient.ResponseSpec registerHostResponse = webTestClient.post()
                .uri("/register")
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(hostRegistrationDetails)
                .exchange()
                .expectStatus().isCreated();

        String registerHostResponseBody = registerHostResponse.returnResult(String.class).getResponseBody().blockFirst();
        assertNotNull(registerHostResponseBody);
        hostId = Arrays.stream(registerHostResponseBody.split("\"")).toList().get(2).substring(1).split(",")[0].trim();

        HttpURLConnection connectionHost = null;
        try {
            URL url = new URL("http://localhost:8083/accounts/verify-email/" + hostId);

            connectionHost = (HttpURLConnection) url.openConnection();
            connectionHost.setRequestMethod("GET");

            connectionHost.setDoOutput(true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader ignored = new BufferedReader(new InputStreamReader(connectionHost.getInputStream(), StandardCharsets.UTF_8))) {
            connectionHost.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loginHost() {
        WebTestClient.ResponseSpec loginHostResponse = webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(hostLoginDetails)
                .exchange()
                .expectStatus().isOk();

        loginHostResponse.expectHeader().exists(headersConfig.getXsrfCookieHeader());

        String hostResponseBody = loginHostResponse.returnResult(String.class).getResponseBody().blockFirst();
        assertNotNull(hostResponseBody);
        hostJwt = Arrays.stream(hostResponseBody.split("\"")).toList().get(3);
        hostXsrf = Objects.requireNonNull(loginHostResponse.returnResult(String.class)
                        .getResponseHeaders()
                        .get(headersConfig.getXsrfCookieHeader()))
                .getFirst();
    }
}
