package rs.ac.bg.fon.accommodationservice.component;

import rs.ac.bg.fon.accommodationservice.dto.AccommodationDto;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationUnitDto;
import rs.ac.bg.fon.accommodationservice.dto.PriceDto;
import rs.ac.bg.fon.accommodationservice.dto.create.AccommodationDtoCreate;
import rs.ac.bg.fon.accommodationservice.dto.message.MQTransferObject;
import rs.ac.bg.fon.accommodationservice.dto.update.AccommodationUnitDtoUpdate;
import rs.ac.bg.fon.accommodationservice.mapper.AccommodationUnitMapper;
import rs.ac.bg.fon.accommodationservice.mapper.MessageMapper;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.AccommodationUnit;
import rs.ac.bg.fon.accommodationservice.util.ComponentTestBase;
import rs.ac.bg.fon.accommodationservice.util.RabbitListenerTestComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("springboot")
public class AccommodationUnitComponentTest extends ComponentTestBase {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    RabbitListenerTestComponent rabbitListener;

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    AccommodationUnitMapper mapper;

    private final String ACCOMMODATION_URI = "/accommodations";

    public static String JWT_ROLE_HOST_ID_5 = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NSwicm9sZSI6IkhPU1QifQ._WRGrglml83Tpuc_T_2g6isXTEkeob3Tw1B3ekyGr50";
    /*  Info inside of JWT:
    {
           "id": 5,
           "role": "HOST"
    }
     */

    String name = "Hilton Belgrade";
    String unitName = "King size room";
    String updatedUnitName = "Hilton room";



    private AccommodationDtoCreate createAccommodation() {
        return AccommodationDtoCreate.builder()
                .name(name)
                .description("Nice hotel in city center")
                .accommodationType(AccommodationType.HOTEL)
                .build();
    }

    private Long postAccommodation(AccommodationDtoCreate accommodationDto) {
        return webTestClient.post()
                .uri(ACCOMMODATION_URI)
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_HOST_ID_5)
                .bodyValue(accommodationDto)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Long.class)
                .getResponseBody().toStream().findAny().get();
    }

    private AccommodationDto getAccommodation(Long id) {
        return webTestClient.get()
                .uri(ACCOMMODATION_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccommodationDto.class)
                .returnResult().getResponseBody();
    }

    private AccommodationUnitDto createUnit(AccommodationDto accommodationDto) {
        return AccommodationUnitDto.builder()
                .name(unitName)
                .accommodation(accommodationDto)
                .description("Nice cozy room")
                .capacity(2)
                .build();
    }

    private Long postUnit(AccommodationUnitDto unitDto) {
        return webTestClient.post()
                .uri("/rooms")
                .bodyValue(unitDto)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Long.class)
                .getResponseBody().toStream().findAny().get();
    }

    private AccommodationUnitDto getUnit(Long id) {
        return webTestClient.get()
                .uri("/rooms/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccommodationUnitDto.class)
                .returnResult().getResponseBody();
    }


    @Test
    void addAccommodationUnit() {
        AccommodationDtoCreate accommodationDto = createAccommodation();

        Long accommodationDtoId = postAccommodation(accommodationDto);


        accommodationDto.setId(accommodationDtoId);

        AccommodationDto accommodationDto1 = getAccommodation(accommodationDtoId);


        AccommodationUnitDto accommodationUnitDto = createUnit(accommodationDto1);

        Long unitId = postUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);

        AccommodationUnitDto accommodationDtoResult = getUnit(unitId);


        MQTransferObject<Object> object = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        AccommodationUnit accommodationUnit = messageMapper.accommodationUnitMessageDtoToEntity(
                rabbitListener.hashMapToUnit((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "AccommodationUnit");
        assertEquals(accommodationUnit, mapper.domainToEntity(mapper.dtoToDomain(accommodationDtoResult)));

        AccommodationUnitDto accommodationUnitDtoResult = getUnit(unitId);
        assertEquals(accommodationUnitDtoResult.getName(), accommodationUnitDto.getName());
    }

    @Test
    void addAccommodationUnitValuesNull() {
        AccommodationUnitDto accommodationUnitDto = AccommodationUnitDto.builder()
                .name(unitName)
                .description("Nice cozy room")
                .capacity(2)
                .build();

        webTestClient.post()
                .uri("/rooms")
                .bodyValue(accommodationUnitDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateAccommodationUnit() {
        AccommodationDtoCreate accommodationDto = createAccommodation();

        Long id = postAccommodation(accommodationDto);


        accommodationDto.setId(id);

        AccommodationDto accommodationDto1 = getAccommodation(id);

        AccommodationUnitDto accommodationUnitDto = createUnit(accommodationDto1);

        Long unitId = postUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);

        AccommodationUnitDto accommodationDtoResult = getUnit(unitId);


        MQTransferObject<Object> object = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        AccommodationUnit accommodationUnit = messageMapper.accommodationUnitMessageDtoToEntity(
                rabbitListener.hashMapToUnit((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "AccommodationUnit");
        assertEquals(accommodationUnit, mapper.domainToEntity(mapper.dtoToDomain(accommodationDtoResult)));

        assertEquals(accommodationUnit, mapper.domainToEntity(mapper.dtoToDomain(accommodationDtoResult)));


        List<AccommodationUnitDto> result
                = webTestClient.get()
                .uri("/rooms/" + unitId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AccommodationUnitDto>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(result);

        AccommodationUnitDtoUpdate accommodationUnitDtoUpdated = AccommodationUnitDtoUpdate.builder()
                .id(unitId)
                .name(updatedUnitName)
                .description("Huge room")
                .capacity(22)
                .build();


        webTestClient.put()
                .uri("/rooms")
                .bodyValue(accommodationUnitDtoUpdated)
                .exchange()
                .expectStatus().isOk();

        accommodationDtoResult = getUnit(unitId);

        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        accommodationUnit = messageMapper.accommodationUnitMessageDtoToEntity(
                rabbitListener.hashMapToUnit((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "UPDATE");
        assertEquals(object.getEntityType(), "AccommodationUnit");
        assertEquals(accommodationUnit, mapper.domainToEntity(mapper.dtoToDomain(accommodationDtoResult)));

        assertEquals(accommodationUnit, mapper.domainToEntity(mapper.dtoToDomain(accommodationDtoResult)));

        AccommodationUnitDto accommodationUnitDtoResult = getUnit(unitId);
        assertEquals(accommodationUnitDtoResult.getName(), accommodationUnitDtoUpdated.getName());

    }

    @Test
    void updateAccommodationUnitAndPricesNotDeleted() {
        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accommodationId = postAccommodation(accommodationDtoCreate);

        AccommodationDto accommodationDto = getAccommodation(accommodationId);

        AccommodationUnitDto accommodationUnitDto = createUnit(accommodationDto);

        Long unitId = postUnit(accommodationUnitDto);
        accommodationUnitDto.setId(unitId);

        PriceDto price1 = PriceDto.builder()
                .dateFrom(LocalDate.of(2035, 3, 24))
                .dateTo(LocalDate.of(2035, 3, 26))
                .amount(BigDecimal.valueOf(110.00))
                .currency("USD")
                .accommodationUnit(accommodationUnitDto)
                .build();

        PriceDto price2 = PriceDto.builder()
                .dateFrom(LocalDate.of(2035, 3, 27))
                .dateTo(LocalDate.of(2035, 3, 30))
                .amount(BigDecimal.valueOf(115.00))
                .currency("USD")
                .accommodationUnit(accommodationUnitDto)
                .build();

        webTestClient.post()
                .uri("/prices")
                .bodyValue(price1)
                .exchange()
                .expectStatus()
                .isCreated();

        webTestClient.post()
                .uri("/prices")
                .bodyValue(price2)
                .exchange()
                .expectStatus()
                .isCreated();

        List<PriceDto> prices = webTestClient.get()
                .uri("/prices/{unitId}/price", unitId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PriceDto>() {
                })
                .returnResult().getResponseBody();

        assertThat(prices.size()).isEqualTo(2);

        AccommodationUnitDtoUpdate accommodationUnitDtoUpdated = AccommodationUnitDtoUpdate.builder()
                .id(unitId)
                .name(updatedUnitName)
                .description("Huge room")
                .capacity(22)
                .build();

        webTestClient.put()
                .uri("/rooms")
                .bodyValue(accommodationUnitDtoUpdated)
                .exchange()
                .expectStatus().isOk();

        List<PriceDto> pricesAfterUpdated = webTestClient.get()
                .uri("/prices/{unitId}/price", unitId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PriceDto>() {
                })
                .returnResult().getResponseBody();

        assertThat(pricesAfterUpdated.size()).isEqualTo(2);
    }

    @Test
    void deleteAccommodationUnit() {
        AccommodationDtoCreate accommodationDto = createAccommodation();

        Long id = postAccommodation(accommodationDto);


        accommodationDto.setId(id);

        AccommodationDto accommodationDto1 = getAccommodation(id);

        AccommodationUnitDto accommodationUnitDto = createUnit(accommodationDto1);

        Long unitId = postUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);

        AccommodationUnitDto accommodationDtoResult = getUnit(unitId);


        MQTransferObject<Object> object = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        AccommodationUnit accommodationUnit = messageMapper.accommodationUnitMessageDtoToEntity(
                rabbitListener.hashMapToUnit((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "AccommodationUnit");
        assertEquals(accommodationUnit, mapper.domainToEntity(mapper.dtoToDomain(accommodationDtoResult)));

        List<AccommodationUnitDto> result
                = webTestClient.get()
                .uri("/rooms/" + unitId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AccommodationUnitDto>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(result);

        webTestClient.delete()
                .uri("/rooms/{id}", unitId)
                .exchange()
                .expectStatus().isOk();

        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        AccommodationUnit deletedAccommodationUnit = messageMapper.accommodationUnitMessageDtoToEntity(
                rabbitListener.hashMapToUnit((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "UPDATE");
        assertEquals(object.getEntityType(), "AccommodationUnit");
        assertEquals(deletedAccommodationUnit, mapper.domainToEntity(mapper.dtoToDomain(accommodationDtoResult)));

    }

    @Test
    void updateAccommodationThatDoesNotExist() {

        AccommodationDtoCreate accommodationDtoUpdated = AccommodationDtoCreate.builder()
                .id(1L)
                .name("Hilton BGD")
                .description("Nice hotel close to city center")
                .accommodationType(AccommodationType.HOTEL)
                .build();
        Long id = postAccommodation(accommodationDtoUpdated);

        accommodationDtoUpdated.setId(id);

        AccommodationUnitDtoUpdate accommodationUnitUpdated = AccommodationUnitDtoUpdate.builder()
                .id(0L)
                .name(updatedUnitName)
                .description("Huge room")
                .capacity(22)
                .build();

        webTestClient.put()
                .uri("/rooms")
                .bodyValue(accommodationUnitUpdated)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getUnitsByHostId() {
        AccommodationDtoCreate accommodationDto = createAccommodation();
        Long id = postAccommodation(accommodationDto);
        AccommodationDto savedAccommodationDto = getAccommodation(id);

        AccommodationUnitDto accommodationUnitDto1 = createUnit(savedAccommodationDto);
        Long unitId1 = postUnit(accommodationUnitDto1);

        AccommodationUnitDto accommodationUnitDto2 = createUnit(savedAccommodationDto);
        Long unitId2 = postUnit(accommodationUnitDto2);

        String expectedResult = """
                [
                {
                "id":""" + unitId1 + """
                ,
                "name":"King size room",
                "description":"Nice cozy room",
                "capacity":2,
                "accommodation":
                    {
                        "id":""" + id + """
                        ,
                        "name":"Hilton Belgrade",
                        "description":"Nice hotel in city center",
                        "accommodationType":"HOTEL",
                        "hostId":5,
                        "amenities":[]
                    }
                },
                {
                "id":""" + unitId2 + """
                ,
                "name":"King size room",
                "description":"Nice cozy room",
                "capacity":2,
                "accommodation":
                    {
                        "id":""" + id + """
                        ,
                        "name":"Hilton Belgrade",
                        "description":"Nice hotel in city center",
                        "accommodationType":"HOTEL",
                        "hostId":5,
                        "amenities":[]
                    }
                }
                ]
                """;

        webTestClient.get()
                .uri("/rooms/my-rooms")
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_HOST_ID_5)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedResult);

    }
}
