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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Tag("springboot")
public class ReservationIntegrationTest {
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
    private static String accommodationId;
    private static String accommodationUnitId;

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

        createAccommodationAndUnitAndPrice();
    }

    @Test
    void postGetPatchDeleteReservation() {
        String reservation = """
                {
                "dateRange": {
                "checkInDate": "2034-01-01",
                "checkOutDate": "2034-01-03"
                },
                "totalAmount": 20.0,
                "numberOfPeople": 2,
                "profileId":\s""" + userId + """
                ,
                "accommodationUnitId":\s""" + accommodationUnitId + """
                }
                """;
        WebTestClient.ResponseSpec reservationResponse = webTestClient.post()
                .uri("/reservations")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(reservation)
                .exchange()
                .expectStatus().isCreated();

        String reservationId = reservationResponse.returnResult(String.class).getResponseBody().blockFirst();

        String expectedReservation =
                "[{" +
                        "\"id\":" + reservationId + "," +
                        "\"creationDate\":\"" + LocalDate.now() +
                        "\"," +
                        "\"dateRange\":{" +
                        "\"checkInDate\":\"2034-01-01\"," +
                        "\"checkOutDate\":\"2034-01-03\"" +
                        "}," +
                        "\"totalAmount\":200," +
                        "\"status\":\"ACTIVE\"," +
                        "\"numberOfPeople\":2," +
                        "\"profileId\":" + userId + "," +
                        "\"accommodationUnitId\":" + accommodationUnitId + "," +
                        "\"currency\":" + "\"EUR\"" + "," +
                        "\"paid\":" + "false" + "}]";

        webTestClient.get()
                .uri("/reservations/" + userId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1)
                .jsonPath("$.content").value(v -> assertEquals(expectedReservation, v.toString()));

        webTestClient.patch()
                .uri("/reservations/" + reservationId + "/process-payment")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isOk();

        String expectedReservationPaid =
                "[{" +
                        "\"id\":" + reservationId + "," +
                        "\"creationDate\":\"" + LocalDate.now() +
                        "\"," +
                        "\"dateRange\":{" +
                        "\"checkInDate\":\"2034-01-01\"," +
                        "\"checkOutDate\":\"2034-01-03\"" +
                        "}," +
                        "\"totalAmount\":200," +
                        "\"status\":\"ACTIVE\"," +
                        "\"numberOfPeople\":2," +
                        "\"profileId\":" + userId + "," +
                        "\"accommodationUnitId\":" + accommodationUnitId + "," +
                        "\"currency\":" + "\"EUR\"" + "," +
                        "\"paid\":" + "true" + "}]";

        webTestClient.get()
                .uri("/reservations/" + userId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1)
                .jsonPath("$.content").value(v -> assertEquals(expectedReservationPaid, v.toString()));

        webTestClient.delete()
                .uri("/reservations/" + reservationId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/reservations/" + userId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(0);
    }

    @Test
    void pendingRequestConfirmed() {
        String reservation = """
                {
                "dateRange": {
                "checkInDate": "2034-01-01",
                "checkOutDate": "2034-01-03"
                },
                "totalAmount": 20.0,
                "numberOfPeople": 2,
                "profileId":\s""" + userId + """
                ,
                "accommodationUnitId":\s""" + accommodationUnitId + """
                }
                """;
        WebTestClient.ResponseSpec reservationResponse = webTestClient.post()
                .uri("/reservations")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(reservation)
                .exchange()
                .expectStatus().isCreated();

        String reservationId = reservationResponse.returnResult(String.class).getResponseBody().blockFirst();

        String pendingReqest = """
                {
                "dateRange": {
                "checkInDate": "2034-03-01",
                "checkOutDate": "2034-03-03"
                },
                "message": "my message"
                }
                """;

        Object pendingRequestId = webTestClient.post()
                .uri("/reservations/" + reservationId + "/pendingRequests")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pendingReqest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<Object>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(pendingRequestId).isNotNull();

        webTestClient.put()
                .uri("/reservations/" + reservationId + "/pendingRequests?requestStatus=CONFIRMED")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk();

        String updatedExpectedReservation =
                "[{" +
                        "\"id\":" + reservationId + "," +
                        "\"creationDate\":\"" + LocalDate.now() +
                        "\"," +
                        "\"dateRange\":{" +
                        "\"checkInDate\":\"2034-03-01\"," +
                        "\"checkOutDate\":\"2034-03-03\"" +
                        "}," +
                        "\"totalAmount\":200," +
                        "\"status\":\"ACTIVE\"," +
                        "\"numberOfPeople\":2," +
                        "\"profileId\":" + userId + "," +
                        "\"accommodationUnitId\":" + accommodationUnitId + "," +
                        "\"currency\":" + "\"EUR\"" + "," +
                        "\"paid\":" + "false" + "}]";

        webTestClient.get()
                .uri("/reservations/" + userId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1)
                .jsonPath("$.content").value(v -> assertEquals(updatedExpectedReservation, v.toString()));
    }


    @Test
    void pendingRequestDenied() {
        String reservation = """
                {
                "dateRange": {
                "checkInDate": "2034-01-01",
                "checkOutDate": "2034-01-03"
                },
                "totalAmount": 20.0,
                "numberOfPeople": 2,
                "profileId":\s""" + userId + """
                ,
                "accommodationUnitId":\s""" + accommodationUnitId + """
                }
                """;
        WebTestClient.ResponseSpec reservationResponse = webTestClient.post()
                .uri("/reservations")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(reservation)
                .exchange()
                .expectStatus().isCreated();

        String reservationId = reservationResponse.returnResult(String.class).getResponseBody().blockFirst();

        String pendingReqest = """
                {
                "dateRange": {
                "checkInDate": "2034-03-01",
                "checkOutDate": "2034-03-03"
                },
                "message": "my message"
                }
                """;
        webTestClient.post()
                .uri("/reservations/" + reservationId + "/pendingRequests")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pendingReqest)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.put()
                .uri("/reservations/" + reservationId + "/pendingRequests?requestStatus=DENIED")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk();

        String updatedExpectedReservation =
                "[{" +
                        "\"id\":" + reservationId + "," +
                        "\"creationDate\":\"" + LocalDate.now() +
                        "\"," +
                        "\"dateRange\":{" +
                        "\"checkInDate\":\"2034-01-01\"," +
                        "\"checkOutDate\":\"2034-01-03\"" +
                        "}," +
                        "\"totalAmount\":200," +
                        "\"status\":\"ACTIVE\"," +
                        "\"numberOfPeople\":2," +
                        "\"profileId\":" + userId + "," +
                        "\"accommodationUnitId\":" + accommodationUnitId + "," +
                        "\"currency\":" + "\"EUR\"" + "," +
                        "\"paid\":" + "false" + "}]";

        webTestClient.get()
                .uri("/reservations/" + userId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1)
                .jsonPath("$.content").value(v -> assertEquals(updatedExpectedReservation, v.toString()));
    }

    @Test
    void postGetReview() {
        String reservation = """
                {
                "dateRange": {
                "checkInDate": "2034-01-01",
                "checkOutDate": "2034-01-03"
                },
                "totalAmount": 20.0,
                "numberOfPeople": 2,
                "profileId":\s""" + userId + """
                ,
                "accommodationUnitId":\s""" + accommodationUnitId + """
                }
                """;
        WebTestClient.ResponseSpec reservationResponse = webTestClient.post()
                .uri("/reservations")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(reservation)
                .exchange()
                .expectStatus().isCreated();

        String reservationId = reservationResponse.returnResult(String.class).getResponseBody().blockFirst();

        String review = """
                {
                    "title": "title",
                    "comment": "comment",
                    "rating": 9.0
                }
                """;

        webTestClient.post()
                .uri("/reservations/" + reservationId + "/reviews")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(review)
                .exchange()
                .expectStatus().isCreated();

        List<Object> reviews = webTestClient.get()
                .uri("/accommodations/" + accommodationId + "/reviews")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(reviews.size()).isEqualTo(1);
    }

    @Test
    void unauthorized() {
        webTestClient.post()
                .uri("/reservations")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.post()
                .uri("/reservations/1/pendingRequests")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.post()
                .uri("/reservations/1/reviews")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.put()
                .uri("/reservations/1/pendingRequests?requestStatus=DENIED")
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

    private void createAccommodationAndUnitAndPrice() {
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

        accommodationId = postAccResponse.returnResult(String.class).getResponseBody().blockFirst();

        String unit = """
                {
                    "name": "my unit",
                    "description": "unit description",
                    "capacity": 2,
                    "accommodation": {
                        "id":\s""" + accommodationId + """
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

        accommodationUnitId = postUnitResponse.returnResult(String.class).getResponseBody().blockFirst();

        String currentDate = LocalDate.now().toString();

        String price = String.format("""
                {
                  "amount": 100,
                  "dateFrom": "%s",
                  "dateTo": "2045-04-25",
                  "currency": "EUR",
                  "accommodationUnit": {
                    "id": %s,
                    "name": "string",
                    "description": "string",
                    "capacity": 3,
                    "accommodation": {
                      "id": %s,
                      "name": "string",
                      "description": "string",
                      "accommodationType": "HOTEL",
                      "hostId": %s,
                      "amenities": []
                    }
                  }
                }
                """, currentDate, accommodationUnitId, accommodationId, hostId);

        webTestClient.post()
                .uri("/accommodations/rooms/" + accommodationUnitId + "/prices")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(price)
                .exchange()
                .expectStatus().isCreated();
    }

    @AfterEach
    void afterEach() {
        webTestClient.delete()
                .uri("/accommodations/" + accommodationId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk();

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
