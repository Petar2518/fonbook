package rs.ac.bg.fon.accommodationservice.component;

import rs.ac.bg.fon.accommodationservice.dto.AccommodationDto;
import rs.ac.bg.fon.accommodationservice.dto.ImageDto;
import rs.ac.bg.fon.accommodationservice.dto.create.AccommodationDtoCreate;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.util.ComponentTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("springboot")
public class ImageComponentTest extends ComponentTestBase {
    @Autowired
    WebTestClient webTestClient;

    private final String IMAGE_URI = "/images";

    String name = "Hilton Belgrade";
    public static String JWT_ROLE_HOST_ID_5 = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NSwicm9sZSI6IkhPU1QifQ._WRGrglml83Tpuc_T_2g6isXTEkeob3Tw1B3ekyGr50";

    /*  Info inside of JWT:
    {
           "id": 5,
           "role": "HOST"
    }
     */

    private AccommodationDtoCreate createAccommodation() {
        return AccommodationDtoCreate.builder()
                .name(name)
                .description("Nice hotel in city center")
                .accommodationType(AccommodationType.HOTEL)
                .build();
    }

    private Long postAccommodation(AccommodationDtoCreate accommodationDto) {
        return webTestClient.post()
                .uri("/accommodations")
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_HOST_ID_5)
                .bodyValue(accommodationDto)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Long.class)
                .getResponseBody().toStream().findAny().get();
    }

    private AccommodationDto getAccommodation(Long id) {
        return webTestClient.get()
                .uri("/accommodations/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccommodationDto.class)
                .returnResult().getResponseBody();
    }

    private ImageDto createImage(AccommodationDto accommodationDto) {
        return ImageDto.builder()
                .image("We are checking. . .".getBytes())
                .accommodation(accommodationDto)
                .build();
    }

    private Long postImage(ImageDto imageDto) {
        return webTestClient.post()
                .uri(IMAGE_URI)
                .bodyValue(imageDto)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Long.class)
                .getResponseBody().toStream().findAny().get();
    }

    private ImageDto getImage(Long id) {
        return webTestClient.get()
                .uri(IMAGE_URI + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ImageDto.class)
                .returnResult().getResponseBody();
    }

    @Test
    void addImage() {
        AccommodationDtoCreate accommodationDto = createAccommodation();

        Long id = postAccommodation(accommodationDto);

        AccommodationDto accommodationDtoResult = getAccommodation(id);

        ImageDto imageDto = createImage(accommodationDtoResult);

        Long imageId = postImage(imageDto);


        ImageDto result = getImage(imageId);

        assertEquals(Arrays.toString(result.getImage()), Arrays.toString(imageDto.getImage()));
        assertEquals(result.getAccommodation().getId(), imageDto.getAccommodation().getId());
    }

    @Test
    void addImageValuesNull() {
        ImageDto imageDto = ImageDto.builder()
                .image("We are checking. . .".getBytes())
                .build();

        webTestClient.post()
                .uri(IMAGE_URI)
                .bodyValue(imageDto)
                .exchange()
                .expectStatus().isBadRequest();


    }

    @Test
    void deleteImage() {
        AccommodationDtoCreate accommodationDto = createAccommodation();

        Long id = postAccommodation(accommodationDto);

        AccommodationDto accommodationDtoResult = getAccommodation(id);

        ImageDto imageDto = createImage(accommodationDtoResult);

        Long imageId = postImage(imageDto);

        ImageDto result
                = getImage(imageId);

        assertEquals(Arrays.toString(result.getImage()), Arrays.toString(imageDto.getImage()));
        assertEquals(result.getAccommodation().getId(), imageDto.getAccommodation().getId());

        webTestClient.delete()
                .uri(IMAGE_URI + "/{id}", imageId)
                .exchange()
                .expectStatus().isOk();

    }


}
