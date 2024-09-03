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
public class AccommodationIntegrationTest {

    @Autowired
    private RequestHeadersConfig headersConfig;
    @Autowired
    private WebTestClient webTestClient;
    private static String userJwt;
    private static String hostJwt;
    private static String userId;
    private static String hostId;
    private static String userXsrf;
    private static String hostXsrf;

    @BeforeEach
    public void setup() {
        RegistrationDto userRegistrationDetails = RegistrationDto.builder()
                .email("useremail@gmail.com")
                .password("password")
                .role("USER")
                .firstName("name")
                .lastName("last name")
                .build();
        String userLoginDetails = """
                {
                    "email": "useremail@gmail.com",
                    "password": "password"
                }
                """;
        RegistrationDto hostRegistrationDetails = RegistrationDto.builder()
                .email("hostemail@gmail.com")
                .password("password")
                .role("HOST")
                .name("name")
                .phoneNumber("0")
                .bankAccountNumber("000000000")
                .build();
        String hostLoginDetails = """
                {
                    "email": "hostemail@gmail.com",
                    "password": "password"
                }
                """;

        registerAndLoginUser(userRegistrationDetails, userLoginDetails);

        registerAndLoginHost(hostRegistrationDetails, hostLoginDetails);
    }

    @Test
    void crudAccommodation() {
        String postAcc = """
                {
                    "name": "name",
                    "description": "desc",
                    "accommodationType": "HOTEL",
                    "hostId":\s""" + hostId + """
                    ,
                    "amenities": []
                }
                """;

        WebTestClient.ResponseSpec postAccResponse = webTestClient.post()
                .uri("/accommodations")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(postAcc)
                .exchange()
                .expectStatus().isCreated();

        String accId = postAccResponse.returnResult(String.class).getResponseBody().blockFirst();

        String expectedAcc = """
                {
                    "id":\s""" + accId + """
                ,
                "name": "name",
                "description": "desc",
                "accommodationType": "HOTEL",
                "hostId":\s""" + hostId + """
                    ,
                    "amenities": []
                }
                """;

        webTestClient.get()
                .uri("/accommodations/" + accId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedAcc);

        webTestClient.get()
                .uri("/my-accommodations/" + hostId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[\n" + expectedAcc + "\n]");

        String updateAcc = """
                {
                    "id":\s""" + accId + """
                ,
                "name": "name",
                "description": "desc",
                "accommodationType": "HOTEL",
                "hostId": \s""" + hostId + """
                    ,
                    "amenities": []
                }
                """;

        webTestClient.put()
                .uri("/accommodations")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateAcc)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/accommodations/" + accId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(updateAcc);

        webTestClient.delete()
                .uri("/accommodations/" + accId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/accommodations/" + accId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void crudAccommodationUnit() {
        String postAcc = """
                {
                    "name": "name",
                    "description": "desc",
                    "accommodationType": "HOTEL",
                    "hostId":\s""" + hostId + """
                    ,
                    "amenities": []
                }
                """;

        WebTestClient.ResponseSpec postAccResponse = webTestClient.post()
                .uri("/accommodations")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(postAcc)
                .exchange()
                .expectStatus().isCreated();

        String accId = postAccResponse.returnResult(String.class).getResponseBody().blockFirst();

        String unit = """
                {
                    "name": "my unit",
                    "description": "unit description",
                    "capacity": 2,
                    "accommodation": {
                        "id":\s""" + accId + """
                    }
                }
                """;

        WebTestClient.ResponseSpec postUnitResponse = webTestClient.post()
                .uri("/accommodations/rooms")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(unit)
                .exchange()
                .expectStatus().isCreated();

        String unitId = postUnitResponse.returnResult(String.class).getResponseBody().blockFirst();

        WebTestClient.ResponseSpec unitGetRes = webTestClient.get()
                .uri("/accommodations/rooms/" + unitId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk();
        unitGetRes.expectBody().json(unit);

        String updatedUnit = """
                {
                    "id":\s""" + unitId + """
                ,
                "name": "my unit",
                "description": "new unit description",
                "capacity": 5,
                "accommodation": {
                    "id":\s""" + accId + """
                    }
                }
                """;

        webTestClient.put()
                .uri("/accommodations/rooms")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedUnit)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/accommodations/rooms/" + unitId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(updatedUnit);

        webTestClient.get()
                .uri("/accommodations/" + accId + "/rooms")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[" + updatedUnit + "]");

        webTestClient.delete()
                .uri("/accommodations/rooms/" + unitId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/accommodations/rooms/" + unitId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isNotFound();

        webTestClient.get()
                .uri("/accommodations/" + accId + "/rooms")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");

        webTestClient.delete()
                .uri("/accommodations/" + accId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void userAuthorization() {

        webTestClient.post()
                .uri("/accommodations")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.put()
                .uri("/accommodations")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.delete()
                .uri("/accommodations/1")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.get()
                .uri("/my-accommodations/1")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.post()
                .uri("/accommodations/rooms")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.put()
                .uri("/accommodations/rooms")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.delete()
                .uri("/accommodations/rooms/1")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    private void registerAndLoginUser(RegistrationDto userRegistrationDetails, String userLoginDetails) {

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

    private void registerAndLoginHost(RegistrationDto hostRegistrationDetails, String hostLoginDetails) {

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

    @AfterEach
    void afterAll() {
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
}
