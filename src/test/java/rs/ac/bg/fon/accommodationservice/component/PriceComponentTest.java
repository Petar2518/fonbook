package rs.ac.bg.fon.accommodationservice.component;

import rs.ac.bg.fon.accommodationservice.dto.AccommodationDto;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationUnitDto;
import rs.ac.bg.fon.accommodationservice.dto.PriceDto;
import rs.ac.bg.fon.accommodationservice.dto.create.AccommodationDtoCreate;
import rs.ac.bg.fon.accommodationservice.dto.message.MQTransferObject;
import rs.ac.bg.fon.accommodationservice.dto.update.PriceDtoUpdate;
import rs.ac.bg.fon.accommodationservice.mapper.MessageMapper;
import rs.ac.bg.fon.accommodationservice.mapper.PriceMapper;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.Price;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("springboot")
public class PriceComponentTest extends ComponentTestBase {


    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    RabbitListenerTestComponent rabbitListener;

    @Autowired
    PriceMapper mapper;

    private final String ACCOMMODATION_URI = "/accommodations";

    private final String PRICE_URI = "/prices";

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

    private AccommodationUnitDto createAccommodationUnit(AccommodationDto accommodationDto) {
        return AccommodationUnitDto.builder()
                .name(unitName)
                .accommodation(accommodationDto)
                .description("Nice cozy room")
                .capacity(2)
                .build();
    }

    private Long postAccommodationUnit(AccommodationUnitDto accommodationUnitDto) {
        return webTestClient.post()
                .uri("/rooms")
                .bodyValue(accommodationUnitDto)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Long.class)
                .getResponseBody().toStream().findAny().get();
    }

    private PriceDto createPrice(AccommodationUnitDto accommodationUnitDto) {
        return PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 24))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();
    }

    private Long postPrice(PriceDto priceDto) {
        return webTestClient.post()
                .uri(PRICE_URI)
                .bodyValue(priceDto)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Long.class)
                .getResponseBody().toStream().findAny().get();
    }

    private PriceDto getPriceById(Long id) {
        return webTestClient.get()
                .uri(PRICE_URI + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PriceDto.class)
                .returnResult().getResponseBody();
    }

    String name = "Hilton Belgrade";

    String unitName = "King size room";

    @Test
    void addPrice() {
        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accId = postAccommodation(accommodationDtoCreate);
        accommodationDtoCreate.setId(accId);

        AccommodationDto accommodationDto = getAccommodation(accId);

        AccommodationUnitDto accommodationUnitDto = createAccommodationUnit(accommodationDto);

        Long unitId = postAccommodationUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);

        PriceDto priceDto = createPrice(accommodationUnitDto);

        Long id = postPrice(priceDto);

        priceDto.setId(id);

        PriceDto priceDtoResult = getPriceById(id);
        MQTransferObject<Object> object = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Price price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Price");

        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(priceDtoResult)));


        assertEquals(Objects.requireNonNull(priceDtoResult).getAmount(), priceDto.getAmount());
        assertEquals(Objects.requireNonNull(priceDtoResult).getDateFrom(), priceDto.getDateFrom());
        assertEquals(Objects.requireNonNull(priceDtoResult).getDateTo(), priceDto.getDateTo());

    }

    @Test
    void addPriceInvalidDate() {
        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accId = postAccommodation(accommodationDtoCreate);

        accommodationDtoCreate.setId(accId);

        AccommodationDto accommodationDto = getAccommodation(accId);


        AccommodationUnitDto accommodationUnitDto = createAccommodationUnit(accommodationDto);

        Long unitId = postAccommodationUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);

        PriceDto priceDto = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 28))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();

        webTestClient.post()
                .uri(PRICE_URI)
                .bodyValue(priceDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void addPriceValuesNull() {
        PriceDto priceDto = createPrice(null);

        webTestClient.post()
                .uri(PRICE_URI)
                .bodyValue(priceDto)
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void updatePrice() {
        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accId = postAccommodation(accommodationDtoCreate);

        accommodationDtoCreate.setId(accId);

        AccommodationDto accommodationDto = getAccommodation(accId);

        AccommodationUnitDto accommodationUnitDto = createAccommodationUnit(accommodationDto);

        Long unitId = postAccommodationUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);

        PriceDto priceDto = createPrice(accommodationUnitDto);

        Long id = postPrice(priceDto);

        priceDto.setId(id);

        PriceDto priceDtoResult = getPriceById(id);


        MQTransferObject<Object> object = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Price price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Price");
        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(priceDtoResult)));

        List<PriceDto> result
                = webTestClient.get()
                .uri(PRICE_URI + "/" + unitId + "/price")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PriceDto>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(result);

        PriceDtoUpdate priceDtoUpdated = PriceDtoUpdate.builder()
                .id(id)
                .dateFrom(LocalDate.of(2025, 4, 23))
                .dateTo(LocalDate.of(2025, 4, 26))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .build();


        webTestClient.put()
                .uri(PRICE_URI)
                .bodyValue(priceDtoUpdated)
                .exchange()
                .expectStatus().isOk();

        priceDtoResult = getPriceById(id);


        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "UPDATE");
        assertEquals(object.getEntityType(), "Price");
        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(priceDtoResult)));


        assertEquals(Objects.requireNonNull(priceDtoResult).getAmount(), priceDtoUpdated.getAmount());
        assertEquals(Objects.requireNonNull(priceDtoResult).getDateFrom(), priceDtoUpdated.getDateFrom());
        assertEquals(Objects.requireNonNull(priceDtoResult).getDateTo(), priceDtoUpdated.getDateTo());


    }

    @Test
    void deletePrice() {
        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accId = postAccommodation(accommodationDtoCreate);

        accommodationDtoCreate.setId(accId);

        AccommodationDto accommodationDto = getAccommodation(accId);

        AccommodationUnitDto accommodationUnitDto = createAccommodationUnit(accommodationDto);

        Long unitId = postAccommodationUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);

        PriceDto priceDto = createPrice(accommodationUnitDto);

        Long id = postPrice(priceDto);

        priceDto.setId(id);

        PriceDto priceDtoResult = getPriceById(id);

        MQTransferObject<Object> object = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Price price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Price");
        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(priceDtoResult)));

        List<PriceDto> result
                = webTestClient.get()
                .uri(PRICE_URI + "/" + unitId + "/price")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PriceDto>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(result);

        webTestClient.delete()
                .uri(PRICE_URI + "/" + id)
                .exchange()
                .expectStatus().isOk();


        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "UPDATE");
        assertEquals(object.getEntityType(), "Price");
        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(priceDtoResult)));
    }

    @Test
    void createPriceNewDateBetweenOld() {

        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accId = postAccommodation(accommodationDtoCreate);

        accommodationDtoCreate.setId(accId);

        AccommodationDto accommodationDto = getAccommodation(accId);

        AccommodationUnitDto accommodationUnitDto = createAccommodationUnit(accommodationDto);

        Long unitId = postAccommodationUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);


        PriceDto priceDto = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 22))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();


        postPrice(priceDto);

        PriceDto priceDtoNew = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 23))
                .dateTo(LocalDate.of(2025, 3, 25))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();

        webTestClient.post()
                .uri(PRICE_URI)
                .bodyValue(priceDtoNew)
                .exchange()
                .expectStatus().isBadRequest();
        List<PriceDto> result
                = webTestClient.get()
                .uri(PRICE_URI + "/" + unitId + "/price")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PriceDto>() {
                })
                .returnResult()
                .getResponseBody();

        /*
        Insert accommodation, insert accommodation unit, insert initial price
         so we expect 3 messages in total.
         */
        MQTransferObject<Object> object = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Price price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Price");
        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(result.get(0))));

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getDateFrom(), LocalDate.of(2025, 3, 22));
        assertEquals(result.get(0).getAmount(), BigDecimal.valueOf(120.00));
        assertEquals(result.get(0).getDateTo(), LocalDate.of(2025, 3, 26));


    }
    @Test
    void createPrice_NewPriceStartDate_SameDay_OldPriceEndDate() {

        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accId = postAccommodation(accommodationDtoCreate);

        accommodationDtoCreate.setId(accId);

        AccommodationDto accommodationDto = getAccommodation(accId);

        AccommodationUnitDto accommodationUnitDto = createAccommodationUnit(accommodationDto);

        Long unitId = postAccommodationUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);


        PriceDto priceDto = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 22))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();


        postPrice(priceDto);

        PriceDto priceDtoNew = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 26))
                .dateTo(LocalDate.of(2025, 3, 28))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();

        postPrice(priceDtoNew);
        List<PriceDto> result
                = webTestClient.get()
                .uri(PRICE_URI + "/" + unitId + "/price")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PriceDto>() {
                })
                .returnResult()
                .getResponseBody();

        /*
        Insert accommodation, insert accommodation unit,
        insert initial price, insert new price,
        so we expect 4 messages in total.
        */
        MQTransferObject<Object> initialPrice = null;
        MQTransferObject<Object> newPrice = null;

        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            initialPrice = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            newPrice = rabbitListener.getMqObject().poll(1000,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Price price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) initialPrice.getMessage()));
        assertEquals(initialPrice.getEventType(), "INSERT");
        assertEquals(initialPrice.getEntityType(), "Price");
        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(result.get(0))));


        price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) newPrice.getMessage()));
        assertEquals(newPrice.getEventType(), "INSERT");
        assertEquals(newPrice.getEntityType(), "Price");
        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(result.get(1))));

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getDateFrom(), LocalDate.of(2025, 3, 22));
        assertEquals(result.get(0).getAmount(), BigDecimal.valueOf(120.00));
        assertEquals(result.get(0).getDateTo(), LocalDate.of(2025, 3, 26));

        assertEquals(result.get(1).getDateFrom(), LocalDate.of(2025, 3, 26));
        assertEquals(result.get(1).getAmount(), BigDecimal.valueOf(110.00));
        assertEquals(result.get(1).getDateTo(), LocalDate.of(2025, 3, 28));


    }

    @Test
    void createPrice_NewPriceEndDate_SameDay_OldPriceStartDate() {

        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accId = postAccommodation(accommodationDtoCreate);

        accommodationDtoCreate.setId(accId);

        AccommodationDto accommodationDto = getAccommodation(accId);

        AccommodationUnitDto accommodationUnitDto = createAccommodationUnit(accommodationDto);

        Long unitId = postAccommodationUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);


        PriceDto priceDto = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 26))
                .dateTo(LocalDate.of(2025, 3, 28))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();


        postPrice(priceDto);

        PriceDto priceDtoNew = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 22))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();


        postPrice(priceDtoNew);
        List<PriceDto> result
                = webTestClient.get()
                .uri(PRICE_URI + "/" + unitId + "/price")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PriceDto>() {
                })
                .returnResult()
                .getResponseBody();

        /*
        Insert accommodation, insert accommodation unit,
        insert initial price, insert new price,
        so we expect 4 messages in total.
        */
        MQTransferObject<Object> initialPrice = null;
        MQTransferObject<Object> newPrice = null;

        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            initialPrice = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            newPrice = rabbitListener.getMqObject().poll(1000,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Price price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) initialPrice.getMessage()));
        assertEquals(initialPrice.getEventType(), "INSERT");
        assertEquals(initialPrice.getEntityType(), "Price");
        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(result.get(0))));


        price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) newPrice.getMessage()));
        assertEquals(newPrice.getEventType(), "INSERT");
        assertEquals(newPrice.getEntityType(), "Price");
        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(result.get(1))));

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getDateFrom(), LocalDate.of(2025, 3, 26));
        assertEquals(result.get(0).getAmount(), BigDecimal.valueOf(110.00));
        assertEquals(result.get(0).getDateTo(), LocalDate.of(2025, 3, 28));


        assertEquals(result.get(1).getDateFrom(), LocalDate.of(2025, 3, 22));
        assertEquals(result.get(1).getAmount(), BigDecimal.valueOf(120.00));
        assertEquals(result.get(1).getDateTo(), LocalDate.of(2025, 3, 26));



    }

    @Test
    void createPriceOldEndsAfterNewStarts() {

        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accId = postAccommodation(accommodationDtoCreate);

        accommodationDtoCreate.setId(accId);

        AccommodationDto accommodationDto = getAccommodation(accId);

        AccommodationUnitDto accommodationUnitDto = createAccommodationUnit(accommodationDto);

        Long unitId = postAccommodationUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);


        PriceDto priceDto = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 22))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();


        postPrice(priceDto);

        PriceDto priceDtoNew = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 24))
                .dateTo(LocalDate.of(2025, 3, 29))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();

        webTestClient.post()
                .uri(PRICE_URI)
                .bodyValue(priceDtoNew)
                .exchange()
                .expectStatus().isBadRequest();

        List<PriceDto> result
                = webTestClient.get()
                .uri(PRICE_URI + "/" + unitId + "/price")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PriceDto>() {
                })
                .returnResult()
                .getResponseBody();


        /*
        Insert accommodation, insert accommodation unit, insert initial price
         so we expect 3 messages in total.
         */
        MQTransferObject<Object> object = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Price price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Price");
        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(result.get(0))));
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getDateFrom(), LocalDate.of(2025, 3, 22));
        assertEquals(result.get(0).getAmount(), BigDecimal.valueOf(120.00));
        assertEquals(result.get(0).getDateTo(), LocalDate.of(2025, 3, 26));


    }

    @Test
    void createPriceOldStartsBeforeNewEnds() {

        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accId = postAccommodation(accommodationDtoCreate);

        accommodationDtoCreate.setId(accId);

        AccommodationDto accommodationDto = getAccommodation(accId);

        AccommodationUnitDto accommodationUnitDto = createAccommodationUnit(accommodationDto);

        Long unitId = postAccommodationUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);


        PriceDto priceDto = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 22))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();


        postPrice(priceDto);

        PriceDto priceDtoNew = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 18))
                .dateTo(LocalDate.of(2025, 3, 24))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();

        webTestClient.post()
                .uri(PRICE_URI)
                .bodyValue(priceDtoNew)
                .exchange()
                .expectStatus().isBadRequest();
        List<PriceDto> result
                = webTestClient.get()
                .uri(PRICE_URI + "/" + unitId + "/price")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PriceDto>() {
                })
                .returnResult()
                .getResponseBody();

        /*
        Insert accommodation, insert accommodation unit, insert initial price
         so we expect 3 messages in total.
         */
        MQTransferObject<Object> object = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Price price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Price");
        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(result.get(0))));
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getDateFrom(), LocalDate.of(2025, 3, 22));
        assertEquals(result.get(0).getAmount(), BigDecimal.valueOf(120.00));
        assertEquals(result.get(0).getDateTo(), LocalDate.of(2025, 3, 26));


    }

    @Test
    void createPriceTwoOldInsideOfNewOne() {

        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accId = postAccommodation(accommodationDtoCreate);

        accommodationDtoCreate.setId(accId);

        AccommodationDto accommodationDto = getAccommodation(accId);

        AccommodationUnitDto accommodationUnitDto = createAccommodationUnit(accommodationDto);

        Long unitId = postAccommodationUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);


        PriceDto priceDtoFirst = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 22))
                .dateTo(LocalDate.of(2025, 3, 30))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();


        postPrice(priceDtoFirst);

        PriceDto priceDtoSecond = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 1))
                .dateTo(LocalDate.of(2025, 3, 21))
                .amount(BigDecimal.valueOf(140.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();


        postPrice(priceDtoSecond);

        PriceDto priceDtoNew = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 1))
                .dateTo(LocalDate.of(2025, 3, 30))
                .amount(BigDecimal.valueOf(200.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();

        webTestClient.post()
                .uri(PRICE_URI)
                .bodyValue(priceDtoNew)
                .exchange()
                .expectStatus().isBadRequest();

        List<PriceDto> result
                = webTestClient.get()
                .uri(PRICE_URI + "/" + unitId + "/price")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PriceDto>() {
                })
                .returnResult()
                .getResponseBody();


        /*
        Insert accommodation, insert accommodation unit, insert first price
        insert second price, so we expect 4 messages in total.
         */
        MQTransferObject<Object> object = null;
        MQTransferObject<Object> object2 = null;

        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object2 = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Price price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Price");
        Price price2 = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object2.getMessage()));
        assertEquals(object2.getEventType(), "INSERT");
        assertEquals(object2.getEntityType(), "Price");
        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(result.get(0))));
        assertEquals(price2, mapper.domainToEntity(mapper.dtoToDomain(result.get(1))));
        assertEquals(result.size(), 2);
        assertEquals(result.get(1).getDateFrom(), LocalDate.of(2025, 3, 1));
        assertEquals(result.get(1).getAmount(), BigDecimal.valueOf(140.00));
        assertEquals(result.get(1).getDateTo(), LocalDate.of(2025, 3, 21));
        assertEquals(result.get(0).getDateFrom(), LocalDate.of(2025, 3, 22));
        assertEquals(result.get(0).getAmount(), BigDecimal.valueOf(120.00));
        assertEquals(result.get(0).getDateTo(), LocalDate.of(2025, 3, 30));

    }

    @Test
    void createPricesFullCheckWithDates() {

        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accId = postAccommodation(accommodationDtoCreate);

        accommodationDtoCreate.setId(accId);

        AccommodationDto accommodationDto = getAccommodation(accId);

        AccommodationUnitDto accommodationUnitDto = createAccommodationUnit(accommodationDto);

        Long unitId = postAccommodationUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);


        PriceDto priceDtoFirst = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 5))
                .dateTo(LocalDate.of(2025, 3, 10))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();


        postPrice(priceDtoFirst);

        PriceDto priceDtoSecond = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 11))
                .dateTo(LocalDate.of(2025, 3, 15))
                .amount(BigDecimal.valueOf(140.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();
        PriceDto priceDtoThird = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 16))
                .dateTo(LocalDate.of(2025, 3, 20))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();


        postPrice(priceDtoSecond);

        PriceDto priceDtoForth = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 21))
                .dateTo(LocalDate.of(2025, 3, 25))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();


        postPrice(priceDtoThird);


        postPrice(priceDtoForth);


        PriceDto priceDtoNew = PriceDto.builder()
                .dateFrom(LocalDate.of(2025, 3, 7))
                .dateTo(LocalDate.of(2025, 3, 23))
                .amount(BigDecimal.valueOf(200.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();

        webTestClient.post()
                .uri(PRICE_URI)
                .bodyValue(priceDtoNew)
                .exchange()
                .expectStatus().isBadRequest();

        List<PriceDto> result
                = webTestClient.get()
                .uri(PRICE_URI + "/" + unitId + "/price")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PriceDto>() {
                })
                .returnResult()
                .getResponseBody();

        MQTransferObject<Object> object = null;
        MQTransferObject<Object> object2 = null;
        MQTransferObject<Object> object3 = null;
        MQTransferObject<Object> object4 = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object2 = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object3 = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object4 = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Price price = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Price");
        Price price2 = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object2.getMessage()));
        assertEquals(object2.getEventType(), "INSERT");
        assertEquals(object2.getEntityType(), "Price");
        Price price3 = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object3.getMessage()));
        assertEquals(object3.getEventType(), "INSERT");
        assertEquals(object3.getEntityType(), "Price");
        Price price4 = messageMapper.priceMessageDtoToEntity(
                rabbitListener.hashMapToPrice((LinkedHashMap<?, ?>) object4.getMessage()));
        assertEquals(object4.getEventType(), "INSERT");
        assertEquals(object4.getEntityType(), "Price");
        assertEquals(price, mapper.domainToEntity(mapper.dtoToDomain(result.get(0))));
        assertEquals(price2, mapper.domainToEntity(mapper.dtoToDomain(result.get(1))));
        assertEquals(price3, mapper.domainToEntity(mapper.dtoToDomain(result.get(2))));
        assertEquals(price4, mapper.domainToEntity(mapper.dtoToDomain(result.get(3))));
        assertEquals(result.size(), 4);
        assertEquals(result.get(0).getDateFrom(), LocalDate.of(2025, 3, 5));
        assertEquals(result.get(0).getAmount(), BigDecimal.valueOf(120.00));
        assertEquals(result.get(0).getDateTo(), LocalDate.of(2025, 3, 10));
        assertEquals(result.get(1).getDateFrom(), LocalDate.of(2025, 3, 11));
        assertEquals(result.get(1).getAmount(), BigDecimal.valueOf(140.00));
        assertEquals(result.get(1).getDateTo(), LocalDate.of(2025, 3, 15));
        assertEquals(result.get(2).getDateFrom(), LocalDate.of(2025, 3, 16));
        assertEquals(result.get(2).getAmount(), BigDecimal.valueOf(120.00));
        assertEquals(result.get(2).getDateTo(), LocalDate.of(2025, 3, 20));
        assertEquals(result.get(3).getDateFrom(), LocalDate.of(2025, 3, 21));
        assertEquals(result.get(3).getAmount(), BigDecimal.valueOf(120.00));
        assertEquals(result.get(3).getDateTo(), LocalDate.of(2025, 3, 25));
    }

    @Test
    void createPricesDateBeforeToday() {

        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accId = postAccommodation(accommodationDtoCreate);

        accommodationDtoCreate.setId(accId);

        AccommodationDto accommodationDto = getAccommodation(accId);

        AccommodationUnitDto accommodationUnitDto = createAccommodationUnit(accommodationDto);

        Long unitId = postAccommodationUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);


        PriceDto priceDto = PriceDto.builder()
                .dateFrom(LocalDate.of(2020, 3, 5))
                .dateTo(LocalDate.of(2020, 3, 10))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();


        webTestClient.post()
                .uri(PRICE_URI)
                .bodyValue(priceDto)
                .exchange()
                .expectStatus().isBadRequest();


    }

    @Test
    void updatePriceThatDoesNotExist() {

        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accId = postAccommodation(accommodationDtoCreate);

        accommodationDtoCreate.setId(accId);

        AccommodationDto accommodationDto = getAccommodation(accId);
        AccommodationUnitDto accommodationUnitDto = createAccommodationUnit(accommodationDto);

        Long unitId = postAccommodationUnit(accommodationUnitDto);

        accommodationUnitDto.setId(unitId);


        PriceDtoUpdate priceDtoUpdated = PriceDtoUpdate.builder()
                .id(0L)
                .dateFrom(LocalDate.of(2025, 3, 24))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(120.00))
                .currency("EUR")
                .build();


        webTestClient.put()
                .uri(PRICE_URI)
                .bodyValue(priceDtoUpdated)
                .exchange()
                .expectStatus().isNotFound();

    }

}
