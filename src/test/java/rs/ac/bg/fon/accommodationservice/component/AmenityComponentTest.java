package rs.ac.bg.fon.accommodationservice.component;

import rs.ac.bg.fon.accommodationservice.dto.AmenityDto;
import rs.ac.bg.fon.accommodationservice.util.ComponentTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("springboot")
public class AmenityComponentTest extends ComponentTestBase {
    @Autowired
    WebTestClient webTestClient;

    private final String AMENITY_URI = "/amenities";


    private AmenityDto createAmenity() {
        return AmenityDto.builder()
                .amenity("Pool")
                .build();
    }

    private Long postAmenity(AmenityDto amenityDto) {
        return webTestClient.post()
                .uri(AMENITY_URI)
                .bodyValue(amenityDto)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Long.class)
                .getResponseBody().toStream().findAny().get();
    }

    private AmenityDto getAmenity(Long id) {
        return webTestClient.get()
                .uri(AMENITY_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AmenityDto.class)
                .returnResult().getResponseBody();
    }

    @Test
    void addAmenity() {
        AmenityDto amenityDto = createAmenity();

        Long amenityId = postAmenity(amenityDto);

        amenityDto.setId(amenityId);

        List<AmenityDto> result
                = webTestClient.get()
                .uri(AMENITY_URI + "?page=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AmenityDto>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(result).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id").contains(amenityDto);

        assertNotNull(result);
        Long idFromDatabase = result
                .stream()
                .filter(value -> value.getAmenity().equals(amenityDto.getAmenity()))
                .map(AmenityDto::getId)
                .findAny()
                .orElseThrow();

        assertEquals(idFromDatabase, amenityId);
        amenityDto.setId(idFromDatabase);

        AmenityDto amenityDtoResult = getAmenity(idFromDatabase);
        assertEquals(amenityDtoResult.getAmenity(), amenityDto.getAmenity());
    }

    @Test
    void addAmenityValuesNull() {
        AmenityDto amenityDto = AmenityDto.builder()
                .amenity(null)
                .build();

        webTestClient.post()
                .uri(AMENITY_URI)
                .bodyValue(amenityDto)
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void updateAmenity() {
        AmenityDto amenityDto = createAmenity();

        Long id = postAmenity(amenityDto);

        amenityDto.setId(id);

        List<AmenityDto> result
                = webTestClient.get()
                .uri(AMENITY_URI + "?page=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AmenityDto>() {
                })
                .returnResult()
                .getResponseBody();


        assertNotNull(result);

        AmenityDto amenityDtoUpdated = AmenityDto.builder()
                .id(id)
                .amenity("Kid's pool")
                .build();

        webTestClient.put()
                .uri(AMENITY_URI)
                .bodyValue(amenityDtoUpdated)
                .exchange()
                .expectStatus().isOk();

        AmenityDto amenityDtoResult = getAmenity(id);
        assertEquals(amenityDtoResult.getAmenity(), amenityDtoUpdated.getAmenity());

    }

    @Test
    void deleteAmenity() {
        AmenityDto amenityDto = createAmenity();

        Long id = postAmenity(amenityDto);

        amenityDto.setId(id);

        List<AmenityDto> result
                = webTestClient.get()
                .uri(AMENITY_URI + "?page=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AmenityDto>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(result);

        webTestClient.delete()
                .uri(AMENITY_URI + "/{id}", id)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri(AMENITY_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    void updateAmenityThatDoesNotExist() {

        AmenityDto amenityDtoUpdated = AmenityDto.builder()
                .id(20L)
                .amenity("Kid's pool")
                .build();

        webTestClient.put()
                .uri(AMENITY_URI)
                .bodyValue(amenityDtoUpdated)
                .exchange()
                .expectStatus().isNotFound();
    }
}
