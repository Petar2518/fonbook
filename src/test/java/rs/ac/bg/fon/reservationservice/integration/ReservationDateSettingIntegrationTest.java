package rs.ac.bg.fon.reservationservice.integration;

import rs.ac.bg.fon.reservationservice.dto.CreateReservationDateSettingDto;
import rs.ac.bg.fon.reservationservice.dto.CreateReservationDto;
import rs.ac.bg.fon.reservationservice.dto.ReservationDateSettingDto;
import rs.ac.bg.fon.reservationservice.dto.ReservationDto;
import rs.ac.bg.fon.reservationservice.dto.message.MQTransferObject;
import rs.ac.bg.fon.reservationservice.model.DateRange;
import rs.ac.bg.fon.reservationservice.model.RequestStatus;
import rs.ac.bg.fon.reservationservice.util.IntegrationTestBase;
import rs.ac.bg.fon.reservationservice.util.RabbitListenerTestComponent;
import rs.ac.bg.fon.reservationservice.util.WireMockTestHelper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("springboot")
public class ReservationDateSettingIntegrationTest extends IntegrationTestBase {

    @Autowired
    WebTestClient webTestClient;

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


    @Autowired
    RabbitListenerTestComponent rabbitListener;


    private static final String jwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImlkIjoxNDcsInN1YiI6Im1pbmFjZXJvdmljMTZAZ21haWwuY29tIiwiaWF0IjoxNzExOTY4NDk1LCJleHAiOjE3MTE5NjkwOTV9.kxsyjodCmGDvCujrH1W7LJRDHXS-Da3oo8VaYAsruZU";

    @Test
    void createModifyRequestForReservation_expectSuccess() {
        CreateReservationDto createReservationDto = newCreateReservationDto();
        CreateReservationDateSettingDto createReservationDateSettingDto = newCreateReservationDateSettingDto();

        Long reservationId = postReservation(createReservationDto);

        Long modifyRequestId = postModifyRequest(reservationId, createReservationDateSettingDto);

        assertEquals(reservationId, modifyRequestId);
    }

    @Test
    void changeRequestStatus_expectSuccess() {
        CreateReservationDto createReservationDto = newCreateReservationDto();
        RequestStatus requestStatus = RequestStatus.CONFIRMED;
        CreateReservationDateSettingDto createReservationDateSettingDto = newCreateReservationDateSettingDto();

        Long reservationId = postReservation(createReservationDto);
        postModifyRequest(reservationId, createReservationDateSettingDto);

        putModifyRequest(requestStatus, reservationId);

        MQTransferObject<Object> object = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertNotNull(object);

        ReservationDto reservationDtoFromRabbit = rabbitListener.hashMapToReservationDto((LinkedHashMap<?, ?>) object.getMessage());
        ReservationDto reservationDtoFromDB = getByReservationId(reservationId);

        assertEquals(object.getEntityType(), "Reservation");
        assertEquals(object.getEventType(), "UPDATE");
        assertEquals(reservationDtoFromDB, reservationDtoFromRabbit);
    }

    @Test
    void changeStatus_IfStatusIsConfirmedAndReservationExists_expectException() {
        CreateReservationDto createReservationDto = newCreateReservationDto();
        RequestStatus requestStatus = RequestStatus.CONFIRMED;
        CreateReservationDateSettingDto createReservationDateSettingDto = newCreateReservationDateSettingDto();

        CreateReservationDto createReservationDto2 = newCreateReservationDto();
        createReservationDto2.setDateRange(new DateRange(LocalDate.of(5000, 4, 17), LocalDate.of(5000, 4, 19)));

        Long reservationId = postReservation(createReservationDto);
        postReservation(createReservationDto2);

        createReservationDateSettingDto.setDateRange(createReservationDto2.getDateRange());
        postModifyRequest(reservationId, createReservationDateSettingDto);

        putModifyRequestWithBadRequest(requestStatus, reservationId);
    }

    @Test
    void changeStatus_IfStatusIsConfirmedAndDatesOverlapWithPreviousDates_expectSuccess() {
        CreateReservationDto createReservationDto = newCreateReservationDto();
        RequestStatus requestStatus = RequestStatus.CONFIRMED;
        CreateReservationDateSettingDto createReservationDateSettingDto = newCreateReservationDateSettingDto();

        Long reservationId = postReservation(createReservationDto);

        createReservationDateSettingDto.setDateRange(createReservationDto.getDateRange());
        postModifyRequest(reservationId, createReservationDateSettingDto);

        putModifyRequest(requestStatus, reservationId);
    }

    @Test
    void getById_expectSuccess() {

        CreateReservationDto createReservationDto = newCreateReservationDto();
        Long reservationId = postReservation(createReservationDto);
        CreateReservationDateSettingDto createModifyRequestDto = newCreateReservationDateSettingDto();
        postModifyRequest(reservationId, createModifyRequestDto);

        ReservationDateSettingDto reservationDateSettingDto = getById(reservationId);

        assertEquals(createModifyRequestDto.getDateRange(), reservationDateSettingDto.getDateRange());
        assertEquals(reservationId, reservationDateSettingDto.getId());
        assertEquals(createModifyRequestDto.getMessage(), reservationDateSettingDto.getMessage());

    }

    public ReservationDateSettingDto getById(Long id) {

        return webTestClient.get()
                .uri("/reservations/{reservationId}/pendingRequests", id)
                .exchange()
                .expectBody(ReservationDateSettingDto.class)
                .returnResult()
                .getResponseBody();
    }

    CreateReservationDateSettingDto newCreateReservationDateSettingDto() {
        return CreateReservationDateSettingDto
                .builder()
                .dateRange(new DateRange(LocalDate.of(5000, 4, 9), LocalDate.of(5000, 4, 11)))
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

    public void putModifyRequest(RequestStatus requestStatus, Long reservationId) {
        webTestClient.put()
                .uri("/reservations/{reservationId}/pendingRequests?requestStatus={requestStatus}", reservationId, requestStatus)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    public void putModifyRequestWithBadRequest(RequestStatus requestStatus, Long reservationId) {
        webTestClient.put()
                .uri("/reservations/{reservationId}/pendingRequests?requestStatus={requestStatus}", reservationId, requestStatus)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    public Long postModifyRequest(Long reservationId, CreateReservationDateSettingDto createReservationDateSettingDto) {
        return webTestClient.post()
                .uri("/reservations/{reservationId}/pendingRequests", reservationId)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(createReservationDateSettingDto), CreateReservationDateSettingDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();
    }
    public ReservationDto getByReservationId(Long id) {
        return webTestClient.get()
                .uri("/reservations/{reservationId}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReservationDto.class)
                .returnResult()
                .getResponseBody();

    }

}
