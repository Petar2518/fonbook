package rs.ac.bg.fon.userservice.integration;

import rs.ac.bg.fon.userservice.dto.UserDto;
import rs.ac.bg.fon.userservice.testcontainers.TestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Tag("springboot")
class UserIntegrationTests extends TestContainer {
    @Autowired
    WebTestClient client;

    @BeforeEach
    void setUp() {
        client.delete().uri("users/10")
                .exchange();
    }

    @Test
    void createNewUser() {

        UserDto userDto = UserDto.builder().userId(10L).firstName("name").lastName("last name").build();

        client.post().uri("/users")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isCreated();

        EntityExchangeResult<UserDto> result = client.get().uri("/users/10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .returnResult();

        assertEquals(result.getResponseBody(), userDto);

    }

    @Test
    void createNewUserAlreadyExists() {

        UserDto userDto = UserDto.builder().userId(10L).firstName("name").lastName("last name").build();

        client.post().uri("/users")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isCreated();

        client.post().uri("/users")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void createNewUserWithNullId() {

        UserDto userDto = UserDto.builder().userId(null).firstName("name").lastName("last name").build();

        client.post().uri("/users")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void deleteUser() {

        UserDto userDto = UserDto.builder().userId(10L).firstName("name").lastName("last name").build();

        client.post().uri("/users")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isCreated();

        client.delete().uri("users/10")
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void updateUser() {

        UserDto userDto = UserDto.builder().userId(10L).firstName("name").lastName("last name").build();

        client.post().uri("/users")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isCreated();

        UserDto newUserDto = UserDto.builder().userId(10L).firstName("new name").lastName("new last name").build();

        client.put().uri("/users")
                .bodyValue(newUserDto)
                .exchange()
                .expectStatus().isOk();

        EntityExchangeResult<UserDto> result = client.get().uri("/users/10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .returnResult();

        assertNotNull(result.getResponseBody());
        assertEquals(userDto.getUserId(), result.getResponseBody().getUserId());
        assertEquals("new name", result.getResponseBody().getFirstName());
        assertEquals("new last name", result.getResponseBody().getLastName());
    }

    @Test
    void updateUserNotFound() {

        UserDto userDto = UserDto.builder().userId(10L).firstName("name").lastName("last name").build();

        client.put().uri("/users")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isNotFound();

    }

}
