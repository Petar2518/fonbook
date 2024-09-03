package rs.ac.bg.fon.bookinggateway.integration;

import rs.ac.bg.fon.bookinggateway.dto.HostDetailsDto;
import rs.ac.bg.fon.bookinggateway.dto.RegistrationDto;
import rs.ac.bg.fon.bookinggateway.dto.UserDetailsDto;
import org.junit.jupiter.api.BeforeAll;
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
class BookingGatewayIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    private static RegistrationDto userRegistrationDetails;
    private static String userLoginDetails;
    private static String userLoginDetailsNewPassword;
    private static RegistrationDto hostRegistrationDetails;
    private static String hostLoginDetails;
    private static String hostLoginDetailsNewPassword;

    @BeforeAll
    public static void setup() {
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
        userLoginDetailsNewPassword = """
                {
                    "email": "useremail@gmail.com",
                    "password": "new password"
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
        hostLoginDetailsNewPassword = """
                {
                    "email": "hostemail@gmail.com",
                    "password": "new password"
                }
                """;
    }

    @Test
    public void manageUserProfile() throws InterruptedException {

        WebTestClient.ResponseSpec registerResponse = webTestClient.post()
                .uri("/register")
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRegistrationDetails)
                .exchange()
                .expectStatus().isCreated();

        String registerResponseBody = registerResponse.returnResult(String.class).getResponseBody().blockFirst();
        assertNotNull(registerResponseBody);
        String id = Arrays.stream(registerResponseBody.split("\"")).toList().get(2).substring(1).split(",")[0].trim();

        verifyEmail(id);

        WebTestClient.ResponseSpec loginResponse = webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userLoginDetails)
                .exchange()
                .expectStatus().isOk();

        loginResponse.expectHeader().exists("X-XSRF-TOKEN");

        String responseBody = loginResponse.returnResult(String.class).getResponseBody().blockFirst();
        assertNotNull(responseBody);
        String jwt = Arrays.stream(responseBody.split("\"")).toList().get(3);

        String xsrf = Objects.requireNonNull(loginResponse.returnResult(String.class).getResponseHeaders().get("X-XSRF-TOKEN")).getFirst();

        UserDetailsDto userInfo1 = UserDetailsDto.builder()
                .userId(Long.valueOf(id))
                .firstName("name")
                .lastName("last name")
                .build();

        webTestClient.get()
                .uri("/users/" + id)
                .header("Authorization", "Bearer " + jwt)
                .header("X-XSRF-TOKEN", xsrf)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDetailsDto.class)
                .isEqualTo(userInfo1);

        String userUpdatePassword = """
                {
                    "accountId":\s""" + id + """
                    ,
                    "oldPassword": "password",
                    "newPassword": "new password"
                }
                """;

        webTestClient.put()
                .uri("/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userUpdatePassword)
                .header("Authorization", "Bearer " + jwt)
                .header("X-XSRF-TOKEN", xsrf)
                .cookie("XSRF-TOKEN", xsrf)
                .exchange()
                .expectStatus().isOk();

        Thread.sleep(1000);

        loginResponse = webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userLoginDetailsNewPassword)
                .exchange()
                .expectStatus().isOk();

        loginResponse.expectHeader().exists("X-XSRF-TOKEN");

        responseBody = loginResponse.returnResult(String.class).getResponseBody().blockFirst();
        assertNotNull(responseBody);
        jwt = Arrays.stream(responseBody.split("\"")).toList().get(3);

        xsrf = Objects.requireNonNull(loginResponse.returnResult(String.class).getResponseHeaders().get("X-XSRF-TOKEN")).getFirst();

        UserDetailsDto userInfo = UserDetailsDto.builder()
                .userId(Long.valueOf(id))
                .firstName("name")
                .lastName("last name")
                .build();

        webTestClient.get()
                .uri("/users/" + id)
                .header("Authorization", "Bearer " + jwt)
                .header("X-XSRF-TOKEN", xsrf)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDetailsDto.class)
                .isEqualTo(userInfo);

        UserDetailsDto userUpdateInfo = UserDetailsDto.builder()
                .userId(Long.valueOf(id))
                .firstName("new name")
                .lastName("new last name")
                .phoneNumber("000")
                .build();

        webTestClient.put()
                .uri("/users")
                .header("Authorization", "Bearer " + jwt)
                .header("X-XSRF-TOKEN", xsrf)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userUpdateInfo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDetailsDto.class)
                .isEqualTo(userUpdateInfo);

        webTestClient.delete()
                .uri("/users/" + id)
                .header("Authorization", "Bearer " + jwt)
                .header("X-XSRF-TOKEN", xsrf)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void manageHostProfile() throws InterruptedException {

        WebTestClient.ResponseSpec registerResponse = webTestClient.post()
                .uri("/register")
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(hostRegistrationDetails)
                .exchange()
                .expectStatus().isCreated();

        String registerResponseBody = registerResponse.returnResult(String.class).getResponseBody().blockFirst();
        assertNotNull(registerResponseBody);
        String id = Arrays.stream(registerResponseBody.split("\"")).toList().get(2).substring(1).split(",")[0].trim();

        verifyEmail(id);

        WebTestClient.ResponseSpec loginResponse = webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(hostLoginDetails)
                .exchange()
                .expectStatus().isOk();

        loginResponse.expectHeader().exists("X-XSRF-TOKEN");

        String responseBody = loginResponse.returnResult(String.class).getResponseBody().blockFirst();
        assertNotNull(responseBody);
        String jwt = Arrays.stream(responseBody.split("\"")).toList().get(3);

        String xsrf = Objects.requireNonNull(loginResponse.returnResult(String.class).getResponseHeaders().get("X-XSRF-TOKEN")).getFirst();

        String hostUpdatePassword = """
                {
                    "accountId":\s""" + id + """
                    ,
                    "oldPassword": "password",
                    "newPassword": "new password"
                }
                """;

        webTestClient.put()
                .uri("/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(hostUpdatePassword)
                .header("Authorization", "Bearer " + jwt)
                .header("X-XSRF-TOKEN", xsrf)
                .cookie("XSRF-TOKEN", xsrf)
                .exchange()
                .expectStatus().isOk();

        Thread.sleep(1000);

        loginResponse = webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(hostLoginDetailsNewPassword)
                .exchange()
                .expectStatus().isOk();

        loginResponse.expectHeader().exists("X-XSRF-TOKEN");

        responseBody = loginResponse.returnResult(String.class).getResponseBody().blockFirst();
        assertNotNull(responseBody);
        jwt = Arrays.stream(responseBody.split("\"")).toList().get(3);

        xsrf = Objects.requireNonNull(loginResponse.returnResult(String.class).getResponseHeaders().get("X-XSRF-TOKEN")).getFirst();

        HostDetailsDto hostInfo = HostDetailsDto.builder()
                .id(Long.valueOf(id))
                .name("name")
                .phoneNumber("0")
                .bankAccountNumber("000000000")
                .build();

        webTestClient.get()
                .uri("/hosts/" + id)
                .header("Authorization", "Bearer " + jwt)
                .header("X-XSRF-TOKEN", xsrf)
                .exchange()
                .expectStatus().isOk()
                .expectBody(HostDetailsDto.class)
                .isEqualTo(hostInfo);

        HostDetailsDto hostUpdateInfo = HostDetailsDto.builder()
                .id(Long.valueOf(id))
                .name("new name")
                .phoneNumber("123456789")
                .bankAccountNumber("123456789")
                .build();

        webTestClient.put()
                .uri("/hosts")
                .header("Authorization", "Bearer " + jwt)
                .header("X-XSRF-TOKEN", xsrf)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(hostUpdateInfo)
                .exchange()
                .expectStatus().isOk();

        webTestClient.delete()
                .uri("/hosts/" + id)
                .header("Authorization", "Bearer " + jwt)
                .header("X-XSRF-TOKEN", xsrf)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void unauthorized() {

        String updatePassword = """
                {
                    "accountId": 1,
                    "oldPassword": "password",
                    "newPassword": "new password"
                }
                """;

        webTestClient.put()
                .uri("/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatePassword)
                .exchange()
                .expectStatus().isForbidden();

        webTestClient.get()
                .uri("/users/1")
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.put()
                .uri("/users")
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.get()
                .uri("/hosts/1")
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.put()
                .uri("/hosts")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    private void verifyEmail(String id) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://localhost:8083/accounts/verify-email/" + id);

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
}