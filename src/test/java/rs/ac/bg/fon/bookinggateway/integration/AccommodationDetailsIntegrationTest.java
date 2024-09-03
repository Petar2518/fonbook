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
public class AccommodationDetailsIntegrationTest {
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

        createAccommodationAndUnit();
    }


    @Test
    void postGetDeleteImages() {
        String image = """
                {
                    "image": [],
                    "accommodation":{
                        "id":\s""" + accommodationId + """
                    }
                }
                """;

        WebTestClient.ResponseSpec postImageResponse = webTestClient.post()
                .uri("/accommodations/" + accommodationId + "/images")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(image)
                .exchange()
                .expectStatus().isCreated();

        String imageId = postImageResponse.returnResult(String.class).getResponseBody().blockFirst();

        String expectedImage = """
                [
                {
                "id":""" + imageId + """
                ,
                "image":"",
                "accommodation":{
                    "id":""" + accommodationId + """
                ,
                "name":"name",
                "description":"desc",
                "accommodationType":"HOTEL",
                "hostId":""" + hostId + """
                            ,
                            "amenities":[]
                    }
                }
                ]
                """;

        webTestClient.get()
                .uri("/accommodations/" + accommodationId + "/images")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedImage);


        webTestClient.delete()
                .uri("/images/" + imageId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/accommodations/" + accommodationId + "/images")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");
    }

    @Test
    void postPutGetAddress() {
        String address = """
                {
                    "id":1,
                    "accommodation": {
                        "id":""" + accommodationId + """
                    },
                    "country":"country",
                    "city": "city",
                    "street": "street",
                    "streetNumber": "1a",
                    "postalCode": "postal code"
                }
                """;

        WebTestClient.ResponseSpec postAddressResponse = webTestClient.post()
                .uri("/accommodations/" + accommodationId + "/address")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(address)
                .exchange()
                .expectStatus().isCreated();

        String addressId = postAddressResponse.returnResult(String.class).getResponseBody().blockFirst();

        String expectedAddress = """
                {
                "id":""" + addressId + """
                ,
                "accommodation":{
                    "id":""" + accommodationId + """
                ,
                "name":"name",
                "description":"desc",
                "accommodationType":"HOTEL",
                "hostId":""" + hostId + """
                            ,
                            "amenities":[]
                    },
                    "country": "country",
                        "city": "city",
                        "street": "street",
                        "streetNumber": "1a",
                        "postalCode": "postal code",
                        "latitude": null,
                        "longitude": null
                }
                """;

        webTestClient.get()
                .uri("/accommodations/" + accommodationId + "/address")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedAddress);

        String updatedAddress = """
                {
                    "id":""" + addressId + """
                ,
                "accommodation": {
                    "id":""" + accommodationId + """
                    },
                    "country":"country",
                    "city": "city",
                    "street": "new street",
                    "streetNumber": "2a",
                    "postalCode": "postal code"
                }
                """;

        webTestClient.put()
                .uri("/accommodations/" + accommodationId + "/address")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedAddress)
                .exchange()
                .expectStatus().isOk();

        String expectedUpdatedAddress = """
                {
                "id":""" + addressId + """
                ,
                "accommodation":{
                    "id":""" + accommodationId + """
                ,
                "name":"name",
                "description":"desc",
                "accommodationType":"HOTEL",
                "hostId":""" + hostId + """
                            ,
                            "amenities":[]
                    },
                    "country": "country",
                        "city": "city",
                        "street": "new street",
                        "streetNumber": "2a",
                        "postalCode": "postal code",
                        "latitude": null,
                        "longitude": null
                }
                """;

        webTestClient.get()
                .uri("/accommodations/" + accommodationId + "/address")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedUpdatedAddress);
    }

    @Test
    void postGetDeletePrice() {
        String price = """
                {
                    "amount": 10.0,
                    "dateFrom": "2027-01-01",
                    "dateTo": "2027-02-01",
                    "currency": "e",
                    "accommodationUnit": {
                        "id":\s""" + accommodationUnitId + """
                    }
                }
                """;

        WebTestClient.ResponseSpec postPriceResponse = webTestClient.post()
                .uri("/accommodations/rooms/" + accommodationUnitId + "/prices")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(price)
                .exchange()
                .expectStatus().isCreated();

        String priceId = postPriceResponse.returnResult(String.class).getResponseBody().blockFirst();

        String expectedPrice = """
                [
                    {
                        "id":\s""" + priceId + """
                ,
                "amount": 10.0,
                "dateFrom": "2027-01-01",
                "dateTo": "2027-02-01",
                "currency": "e",
                "accommodationUnit": {
                    "id":\s""" + accommodationUnitId + """
                ,
                "name": "my unit",
                "description": "unit description",
                "capacity": 2,
                "accommodation": {
                    "id":\s""" + accommodationId + """
                ,
                "name": "name",
                "description": "desc",
                "accommodationType": "HOTEL",
                "hostId":\s""" + hostId + """
                    ,
                    "amenities": []
                   }
                }
                    }
                ]
                """;

        webTestClient.get()
                .uri("/accommodations/rooms/" + accommodationUnitId + "/prices")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedPrice);

        webTestClient.delete()
                .uri("/prices/" + priceId)
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/accommodations/rooms/" + accommodationUnitId + "/prices")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + hostJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");
    }

    @Test
    void userAuthorization() {

        webTestClient.post()
                .uri("/accommodations/" + accommodationId + "/address")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.post()
                .uri("/accommodations/" + accommodationId + "/images")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.post()
                .uri("/accommodations/rooms/" + accommodationUnitId + "/prices")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.delete()
                .uri("/images/1")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.delete()
                .uri("/prices/1")
                .header(headersConfig.getJwtHeader(), headersConfig.getJwtPrefix() + userJwt)
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.put()
                .uri("/accommodations/" + accommodationUnitId + "/address")
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

    private void createAccommodationAndUnit() {
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
    }

    @AfterEach
    void afterEach() {
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
