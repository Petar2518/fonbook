package rs.ac.bg.fon.reservationservice.integration;


import rs.ac.bg.fon.reservationservice.dto.CreateReservationDto;
import rs.ac.bg.fon.reservationservice.dto.CreateReviewDto;
import rs.ac.bg.fon.reservationservice.dto.ReviewDto;
import rs.ac.bg.fon.reservationservice.dto.UpdateReviewDto;
import rs.ac.bg.fon.reservationservice.model.DateRange;
import rs.ac.bg.fon.reservationservice.util.PostgreSqlInitialiser;
import rs.ac.bg.fon.reservationservice.util.WireMockTestHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@AutoConfigureMockMvc
@Transactional
@Tag("springboot")
public class ReviewIntegrationTest extends PostgreSqlInitialiser {

    @Autowired
    WebTestClient webTestClient;
    private static final String jwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImlkIjoxNDcsInN1YiI6Im1pbmFjZXJvdmljMTZAZ21haWwuY29tIiwiaWF0IjoxNzExOTY4NDk1LCJleHAiOjE3MTE5NjkwOTV9.kxsyjodCmGDvCujrH1W7LJRDHXS-Da3oo8VaYAsruZU";

    @BeforeAll
    public static void setUpBeforeAll() {
        WireMockTestHelper.start();
    }

    @BeforeEach
    public void setUp() {
        WireMockTestHelper.setUp();
    }

    @AfterAll
    public static void tearDown() {
        WireMockTestHelper.stop();
    }


    @Test
    void saveReview_expectSuccess() {

        CreateReservationDto createReservationDto = newCreateReservationDto();
        CreateReviewDto createReviewDto = newCreateReviewDto();

        Long reservationId = postReservation(createReservationDto);
        Long reviewId = postReview(createReviewDto, reservationId);

        assertEquals(reviewId, reservationId);
    }

    @Test
    void saveReservation_expectStatus201() {

        CreateReservationDto createReservationDto = newCreateReservationDto();

        postReservation(createReservationDto);

    }

    @Test
    void patchReview_expectSuccess() {

        CreateReservationDto createReservationDto = newCreateReservationDto();
        CreateReviewDto createReviewDto = newCreateReviewDto();

        Long reservationId = postReservation(createReservationDto);
        postReview(createReviewDto, reservationId);

        UpdateReviewDto updateReviewDto = newUpdateReviewDto();
        patchReview(reservationId, updateReviewDto);
    }

    @Test
    void getReviewByReservationId_expectSuccess() {

        CreateReservationDto createReservationDto = newCreateReservationDto();
        CreateReviewDto createReviewDto = newCreateReviewDto();

        Long reservationId = postReservation(createReservationDto);
        Long reviewId = postReview(createReviewDto, reservationId);
        ReviewDto retrievedReviewDto = getReviewByReservationId(reservationId);

        assertEquals(reviewId, reservationId);
        assertEquals(createReviewDto.getComment(), retrievedReviewDto.getComment());
        assertEquals(createReviewDto.getTitle(), retrievedReviewDto.getTitle());
        assertEquals(createReviewDto.getRating(), retrievedReviewDto.getRating());
    }

    @Test
    void getReviewsByAccommodationId() {
        CreateReservationDto createReservationDto = newCreateReservationDto();
        CreateReviewDto createReviewDto = newCreateReviewDto();

        Long reservationId = postReservation(createReservationDto);
        postReview(createReviewDto, reservationId);

        Long accommodationId = 0L;

        List<ReviewDto> reviews = webTestClient.get()
                .uri("/accommodations/{accommodationId}/reviews", accommodationId)
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<ReviewDto>() {
                })
                .returnResult()
                .getResponseBody();

        assertEquals(1, reviews.size());
    }

    public Long postReview(CreateReviewDto createReviewDto, Long reservationId) {
        return webTestClient.post()
                .uri("/reservations/{reservationId}/reviews", reservationId)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(createReviewDto), CreateReviewDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();
    }

    public ReviewDto getReviewByReservationId(Long id) {
        return webTestClient.get()
                .uri("/reservations/{id}/reviews", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReviewDto.class)
                .returnResult()
                .getResponseBody();
    }

    public void patchReview(Long reservationId, UpdateReviewDto updateReviewDto) {
        webTestClient.patch()
                .uri("/reservations/{reservationId}/reviews", reservationId)
                .body(Mono.just(updateReviewDto), UpdateReviewDto.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    public Long postReservation(CreateReservationDto createReservationDto) {

        return webTestClient.post()
                .uri("/reservations")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(createReservationDto), CreateReservationDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();
    }

    public UpdateReviewDto newUpdateReviewDto() {
        return UpdateReviewDto.builder()
                .rating(8.00)
                .build();
    }

    CreateReservationDto newCreateReservationDto() {
        return CreateReservationDto
                .builder()
                .dateRange(new DateRange(LocalDate.of(5000, 4, 11), LocalDate.of(5000, 4, 17)))
                .totalAmount(BigDecimal.ONE)
                .numberOfPeople(1)
                .accommodationUnitId(1L)
                .build();
    }

    CreateReviewDto newCreateReviewDto() {
        return CreateReviewDto.builder()
                .title("Exceptional")
                .comment("This guest didn't leave a comment")
                .rating(9.0)
                .build();
    }
}
