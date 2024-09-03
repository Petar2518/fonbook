package rs.ac.bg.fon.hostservice.integration;

import rs.ac.bg.fon.hostservice.dto.HostDto;
import rs.ac.bg.fon.hostservice.repository.HostRepository;
import rs.ac.bg.fon.hostservice.utill.container.PostgreSqlInitialiser;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@Tag("springboot")
@AutoConfigureMockMvc
@Transactional
public class HostIntegrationTest extends PostgreSqlInitialiser {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    HostRepository hostRepository;


    @Test
    void saveHost_expectStatus201() {

        HostDto hostDto = createTestHostDto(1L);
        postHost(hostDto);

    }

    @Test
    void getById_returnCorrectHost() {

        HostDto expectedHostDto = createTestHostDto(1L);

        postHost(expectedHostDto);
        HostDto retrievedHostDto = getById(expectedHostDto.getId());


        assertEquals(expectedHostDto, retrievedHostDto);
    }
    @Test
    void getAll_returnAll() {

        HostDto expectedHostDto1 = createTestHostDto(1L);
        HostDto expectedHostDto2 = createTestHostDto(2L);

        postHost(expectedHostDto1);
        postHost(expectedHostDto2);

        getAll();
    }

    @Test
    public void getHostById_returnCorrectHost() {

        HostDto expectedHostDto = createTestHostDto(1L);

        postHost(expectedHostDto);
        HostDto retrievedHostDto = getById(expectedHostDto.getId());


        assertEquals(expectedHostDto, retrievedHostDto, "Retrieved HostDto does not match the expected one.");
    }

    @Test
    void updateHost_expectStatus200() {

        HostDto hostDto = createTestHostDto(1L);
        HostDto updatedHostDto = createHostDtoUpdatedByBankAccountNumber(1L);

        postHost(hostDto);
        putHost(updatedHostDto);
    }


    @Test
    void deleteHost_returnStatusNotFound() {

        HostDto hostDto = createTestHostDto(1L);

        postHost(hostDto);
        deleteHost(hostDto.getId());
    }

    private HostDto createHostDtoUpdatedByBankAccountNumber(Long id) {

        return HostDto.builder()
                .id(id)
                .bankAccountNumber("111111111111111")
                .build();
    }

    private HostDto createTestHostDto(Long id) {

        return HostDto.builder()
                .id(id)
                .name("Person1")
                .phoneNumber("123123123")
                .bankAccountNumber("12345626576")
                .build();
    }

    public void postHost(HostDto hostDto) {

        webTestClient.post()
                .uri("/hosts")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(hostDto), HostDto.class)
                .exchange()
                .expectStatus().isCreated();
    }

    public HostDto getById(Long id) {

        return webTestClient.get()
                .uri("/hosts/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(HostDto.class)
                .returnResult()
                .getResponseBody();
    }

    public void putHost(HostDto hostDto) {

        webTestClient.put().uri("/hosts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(hostDto)
                .exchange()
                .expectStatus().isOk();
    }

    public void deleteHost(Long id) {

        webTestClient.delete().uri("/hosts/{id}", id)
                .exchange()
                .expectStatus().isOk();
    }

    public void getAll(){

        webTestClient.get()
                .uri("/hosts")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(2)
                .jsonPath("$.content[0].id").isNotEmpty();
    }

}
