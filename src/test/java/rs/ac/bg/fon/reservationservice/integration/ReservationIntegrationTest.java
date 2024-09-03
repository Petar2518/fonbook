package rs.ac.bg.fon.reservationservice.integration;


import rs.ac.bg.fon.reservationservice.constraints.validators.DateRangeValidator;
import rs.ac.bg.fon.reservationservice.dto.CreateReservationDto;
import rs.ac.bg.fon.reservationservice.dto.ReservationDto;
import rs.ac.bg.fon.reservationservice.dto.message.MQTransferObject;
import rs.ac.bg.fon.reservationservice.feignclient.AccommodationClient;
import rs.ac.bg.fon.reservationservice.mapper.ReservationMapper;
import rs.ac.bg.fon.reservationservice.model.DateRange;
import rs.ac.bg.fon.reservationservice.model.ReservationStatus;
import rs.ac.bg.fon.reservationservice.util.IntegrationTestBase;
import rs.ac.bg.fon.reservationservice.util.RabbitListenerTestComponent;
import rs.ac.bg.fon.reservationservice.util.WireMockTestHelper;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("springboot")
public class ReservationIntegrationTest extends IntegrationTestBase {

    @Autowired
    WebTestClient webTestClient;
    private static final String jwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImlkIjozNSwic3ViIjoibWlsb3N6YXJrb3ZpYzAxQGdtYWlsLmNvbSIsImlhdCI6MTcxMDMyMjQxNywiZXhwIjoxNzEwMzIzMDE3fQ.BtdEvHJt_rusgIo-rtq1UbMmKjgQD2nu6buJPCADXeY";
    /*  Info inside of JWT:
     {
            "id": 35,
            "role": "USER",
            "sub": "miloszarkovic01@gmail.com"
     }
      */
    private static final Long userId = 35L;

    @Autowired
    AccommodationClient accommodationClient;

    @BeforeAll
    public static void setUpBeforeAll() {
        WireMockTestHelper.start();
    }

    @BeforeEach
    public void setUp() {
        WireMockTestHelper.setUp();
    }

    @Mock
    public DateRangeValidator dateRangeValidator;

    @AfterAll
    public static void tearDown() {
        WireMockTestHelper.stop();
    }

    private static final String jwt1 = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwicm9sZSI6IlVTRVIifQ.1VQ9D0r1929TRQi_bVI9YS7R0UoldyKPivHsJw6FvT0";

    /*  Info inside of JWT:
     {
            "id": 1,
            "role": "USER",
            "sub": "miloszarkovic01@gmail.com"
     }
      */

    @Autowired
    RabbitListenerTestComponent rabbitListener;

    @Autowired
    ReservationMapper mapper;

    @Mock
    private Clock clock;

    @Test
    void reserve_ifDatesAreTheSame_reservationAlreadyExists() {
        CreateReservationDto reservation = newCreateReservationDto();
        postReservation(reservation, jwt);
        postAndReturnBadRequest(reservation);

    }

    @Test
    void reserve_ifCheckInIsBeforeExistingDateRange_reservationAlreadyExists() {

        CreateReservationDto reservation = newCreateReservationDto();

        postReservation(reservation, jwt);

        reservation.getDateRange().setCheckInDate(LocalDate.now());
        postAndReturnBadRequest(reservation);

    }

    @Test
    void reserve_ifCheckOutIsAfterExistingDateRange_reservationAlreadyExists() {

        CreateReservationDto reservation = newCreateReservationDto();

        postReservation(reservation, jwt);

        reservation.getDateRange().setCheckOutDate(LocalDate.now().plusDays(11));
        postAndReturnBadRequest(reservation);

    }


    @Test
    void reserve_ifCheckInIsAfterAndCheckOutIsBeforeExistingDateRange_reservationAlreadyExists() {

        CreateReservationDto reservation = newCreateReservationDto();

        postReservation(reservation, jwt);

        reservation.getDateRange().setCheckInDate(LocalDate.now().plusDays(6));
        reservation.getDateRange().setCheckOutDate(LocalDate.now().plusDays(9));

        postAndReturnBadRequest(reservation);

    }

    @Test
    void reserve_ifCheckInIsBeforeAndCheckOutIsAfterExistingDateRange_reservationAlreadyExists() {

        CreateReservationDto reservation = newCreateReservationDto();

        postReservation(reservation, jwt);
        reservation.getDateRange().setCheckInDate(LocalDate.now().plusDays(3));
        reservation.getDateRange().setCheckOutDate(LocalDate.now().plusDays(12));

        postAndReturnBadRequest(reservation);

    }

    @Test
    void reserve_ifCheckOutIsTheSameAsExistingCheckIn_expectSuccess() {

        CreateReservationDto reservation = newCreateReservationDto();

        postReservation(reservation, jwt);

        reservation.getDateRange().setCheckInDate(LocalDate.of(5000, 4, 9));
        reservation.getDateRange().setCheckOutDate(LocalDate.of(5000, 4, 11));
        postReservation(reservation, jwt);

    }

    @Test
    void reserve_ifCheckInIsTheSameAsExistingCheckOut_expectSuccess() {
        CreateReservationDto reservation = newCreateReservationDto();

        postReservation(reservation, jwt);

        reservation.getDateRange().setCheckInDate(LocalDate.of(5000, 4, 17));
        reservation.getDateRange().setCheckOutDate(LocalDate.of(5000, 4, 19));
        postReservation(reservation, jwt);
    }

    @Test
    void saveReservation_expectStatus201() {

        CreateReservationDto createReservationDto = newCreateReservationDto();
        Long id = postReservation(createReservationDto, jwt);


        MQTransferObject<Object> object = null;
        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertNotNull(object);

        ReservationDto reservationDtoFromRabbit = rabbitListener.hashMapToReservationDto((LinkedHashMap<?, ?>) object.getMessage());
        ReservationDto reservationDtoFromDB = getByReservationId(id);

        assertEquals(object.getEntityType(), "Reservation");
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(reservationDtoFromDB, reservationDtoFromRabbit);

    }

    @Test
    void getAll_expectSuccess() {
        CreateReservationDto createReservationDto = newCreateReservationDto();
        CreateReservationDto createReservationDto2 = newCreateReservationDto();

        createReservationDto2.getDateRange().setCheckInDate(LocalDate.of(5000, 4, 9));
        createReservationDto2.getDateRange().setCheckOutDate(LocalDate.of(5000, 4, 11));
        postReservation(createReservationDto, jwt);
        postReservation(createReservationDto2, jwt);

        getAll();


    }

    @Test
    void getById_returnCorrectReservation() {
        CreateReservationDto createReservationDto = newCreateReservationDto();

        Long id = postReservation(createReservationDto, jwt);

        ReservationDto retrievedReservation = getByReservationId(id);

        assertEquals(createReservationDto.getDateRange().getCheckOutDate(), retrievedReservation.getDateRange().getCheckOutDate());
        assertEquals(createReservationDto.getNumberOfPeople(), retrievedReservation.getNumberOfPeople());
        assertEquals(userId, retrievedReservation.getProfileId());
        assertEquals(createReservationDto.getAccommodationUnitId(), retrievedReservation.getAccommodationUnitId());

    }

    @Test
    void getByProfileId_whenBothCheckInAndCheckOutAreProvided_returnAllReservationsBetweenThoseDates() {

        CreateReservationDto createReservationDto = newCreateReservationDto();
        postReservation(createReservationDto, jwt);

        webTestClient.get()
                .uri("/my-reservations?checkInDate{checkInDate}&checkOutDate={checkOut}", createReservationDto.getDateRange().getCheckInDate(), createReservationDto.getDateRange().getCheckOutDate())
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1);

        webTestClient.get()
                .uri("/my-reservations?checkInDate={checkInDate}&checkOutDate={checkOut}", LocalDate.now().plusDays(3), LocalDate.now().plusDays(3))
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(0);
    }

    @Test
    void getByProfileId_whenBothCheckInAndCheckOutAreNull_returnAllReservations() {

        CreateReservationDto createReservationDto = newCreateReservationDto();
        postReservation(createReservationDto, jwt);

        webTestClient.get()
                .uri("/my-reservations")
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1);
    }

    @Test
    void getByProfileId_whenCheckOutIsNull_returnAllReservationsAfterCheckIn() {

        CreateReservationDto createReservationDto = newCreateReservationDto();
        CreateReservationDto createReservationDto2 = newCreateReservationDto();

        createReservationDto2.getDateRange().setCheckInDate(LocalDate.of(5000, 4, 9));
        createReservationDto2.getDateRange().setCheckOutDate(LocalDate.of(5000, 4, 11));

        postReservation(createReservationDto, jwt);
        postReservation(createReservationDto2, jwt);

        getAll();

        webTestClient.get()
                .uri("/my-reservations?checkInDate=5000-04-09")
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(2);


        webTestClient.get()
                .uri("/my-reservations?checkInDate={checkIn}", LocalDate.of(5000, 4, 11))
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1);

        webTestClient.get()
                .uri("/my-reservations?checkInDate={checkIn}", LocalDate.of(5000, 4, 20))
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(0);
    }

    @Test
    void getByProfileId_whenCheckInIsNull_returnAllReservationsBeforeCheckOut() {

        CreateReservationDto createReservationDto = newCreateReservationDto();
        CreateReservationDto createReservationDto2 = newCreateReservationDto();

        createReservationDto2.getDateRange().setCheckInDate(LocalDate.of(5000, 4, 9));
        createReservationDto2.getDateRange().setCheckOutDate(LocalDate.of(5000, 4, 11));

        postReservation(createReservationDto, jwt);
        postReservation(createReservationDto2, jwt);

        webTestClient.get()
                .uri("/my-reservations?checkOutDate={checkOut}", LocalDate.of(5000, 4, 22))
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(2);


        webTestClient.get()
                .uri("/my-reservations?checkOutDate={checkOut}", LocalDate.of(5000, 4, 15))
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(1);

        webTestClient.get()
                .uri("/my-reservations?checkOutDate={checkOut}", LocalDate.of(5000, 4, 10))
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(0);
    }

    @Test
    void cancelReservation_expectSuccess() {

        CreateReservationDto reservation = newCreateReservationDto();

        Long id = postReservation(reservation, jwt);

        cancelReservation(id);

        ReservationDto reservationDto = getByReservationId(id);

        assertEquals(reservationDto.getStatus(), ReservationStatus.CANCELED);


    }

    @Test
    void cancelReservation_expectException() {

        CreateReservationDto reservation = newCreateReservationDto();

        Long id = postReservation(reservation, jwt);

        cancelReservation(id);

        cancelReservationAndReturnBadRequest(id);


    }

    @Test
    void cancelReservation_expectExceptionUnauthorized() {

        CreateReservationDto reservation = newCreateReservationDto();

        Long id = postReservation(reservation, jwt);

        cancelReservationUnauthorized(id);
    }

    @Test
    void deleteReservation_expectSuccess() {
        CreateReservationDto createReservationDto = newCreateReservationDto();
        MQTransferObject<Object> object = null;
        Long id = postReservation(createReservationDto, jwt);
        ReservationDto reservationDtoFromDB = getByReservationId(id);
        deleteReservation(id);

        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertNotNull(object);

        ReservationDto reservationDtoFromRabbit = rabbitListener.hashMapToReservationDto((LinkedHashMap<?, ?>) object.getMessage());

        assertEquals("Reservation", object.getEntityType());
        assertEquals("DELETE", object.getEventType());
        assertEquals(reservationDtoFromDB, reservationDtoFromRabbit);


        getByReservationIdAndReturnStatusNotFound(id);
    }

    @Test
    void processPayment_expectSuccess() {
        Long reservationId = postReservation(newCreateReservationDto(), jwt);

        webTestClient.patch()
                .uri("/reservations/{reservationId}/pay", reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

    }

    @Test
    void processPayment_expectException() {
        Long reservationId = postReservation(newCreateReservationDto(), jwt);

        webTestClient.patch()
                .uri("/reservations/{reservationId}/pay", reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.patch()
                .uri("/reservations/{reservationId}/pay", reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    public void cancelReservation(Long id) {
        webTestClient.put()
                .uri("/reservations/{reservationId}/cancel", id)
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    public void cancelReservationAndReturnBadRequest(Long id) {
        webTestClient.put()
                .uri("/reservations/{reservationId}/cancel", id)
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    public void cancelReservationUnauthorized(Long id) {
        webTestClient.put()
                .uri("/reservations/{reservationId}/cancel", id)
                .header("Authorization", jwt1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    public void postAndReturnBadRequest(CreateReservationDto reservation) {
        webTestClient.post()
                .uri("/reservations")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(reservation), CreateReservationDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }


    public void deleteReservation(Long id) {
        webTestClient.delete().uri("/reservations/{reservationId}", id)
                .exchange()
                .expectStatus().isOk();
    }

    public Long postReservation(CreateReservationDto createReservationDto, String jwt) {

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

    public void getByReservationIdAndReturnStatusNotFound(Long id) {
        webTestClient.get()
                .uri("/reservations/{reservationId}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

    }

    public void getAll() {
        webTestClient.get()
                .uri("/reservations")
                .exchange()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(2)
                .jsonPath("$.content[0].id").isNotEmpty();
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

}
