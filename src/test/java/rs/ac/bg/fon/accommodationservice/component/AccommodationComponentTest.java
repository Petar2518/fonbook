package rs.ac.bg.fon.accommodationservice.component;

import rs.ac.bg.fon.accommodationservice.dto.AccommodationDto;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationUnitDto;
import rs.ac.bg.fon.accommodationservice.dto.AmenityDto;
import rs.ac.bg.fon.accommodationservice.dto.create.AccommodationDtoCreate;
import rs.ac.bg.fon.accommodationservice.dto.message.MQTransferObject;
import rs.ac.bg.fon.accommodationservice.dto.update.AccommodationDtoUpdate;
import rs.ac.bg.fon.accommodationservice.mapper.AccommodationMapper;
import rs.ac.bg.fon.accommodationservice.mapper.MessageMapper;
import rs.ac.bg.fon.accommodationservice.model.Accommodation;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.util.ComponentTestBase;
import rs.ac.bg.fon.accommodationservice.util.RabbitListenerTestComponent;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
@Tag("springboot")
class AccommodationComponentTest extends ComponentTestBase {

    @Autowired
    AccommodationMapper mapper;


    @Autowired
    MessageMapper messageMapper;
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    RabbitListenerTestComponent rabbitListener;

    @Autowired
    HttpServletRequest request;

    private final String ACCOMMODATION_URI = "/accommodations";

    String name = "Hilton Belgrade";
    String updatedName = "Hotel";

    public static String JWT_ROLE_HOST_ID_5 = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NSwicm9sZSI6IkhPU1QifQ._WRGrglml83Tpuc_T_2g6isXTEkeob3Tw1B3ekyGr50";
    /*  Info inside of JWT:
    {
           "id": 5,
           "role": "HOST"
    }
     */

    public static String JWT_ROLE_USER_ID_5 = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImlkIjoxNDcsInN1YiI6Im1pbmFjZXJvdmljMTZAZ21haWwuY29tIiwiaWF0IjoxNzEyMDQzMzgxLCJleHAiOjE3MTIwNDM5ODF9.RGHuuYJ0308eLU2qmpS088RdtA3AGehZL0b2LicYGHM";
    /*  Info inside of JWT:
     {
            "id": 5,
            "role": "USER"
     }
      */
    public static String JWT_ROLE_HOST_ID_6 = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Niwicm9sZSI6IkhPU1QifQ.5H8a2MseWYbQrXK8wkOxehWNr9jE2n4wCDfzSIQdXTc";


    /*  Info inside of JWT:
    {
           "id": 6,
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

    @Test
    void addAccommodation() {
        AccommodationDtoCreate accommodationDto = createAccommodation();

        Long accommodationId = postAccommodation(accommodationDto);

        accommodationDto.setId(accommodationId);

        List<AccommodationDto> result
                = webTestClient.get()
                .uri(ACCOMMODATION_URI + "?page=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AccommodationDto>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(result);
        Long idFromDatabase = result
                .stream()
                .filter(value -> value.getName().equals(accommodationDto.getName()))
                .map(AccommodationDto::getId)
                .findAny()
                .orElseThrow();

        assertEquals(idFromDatabase, accommodationId);
        accommodationDto.setId(idFromDatabase);

        AccommodationDto accommodationDtoResult = getAccommodation(idFromDatabase);

        assertEquals(accommodationDtoResult.getName(), accommodationDto.getName());


        MQTransferObject<Object> object = null;
        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Accommodation accommodation = messageMapper.accommodationMessageDtoToEntity(
                rabbitListener.hashMapToAccommodation((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Accommodation");
        assertEquals(accommodation, mapper.domainToEntity(mapper.dtoToDomain(accommodationDtoResult)));
    }

    @Test
    void addAccommodationValuesNull() {
        AccommodationDto accommodationDto = AccommodationDto.builder()
                .name(name)
                .description("Nice hotel in city center")
                .build();

        webTestClient.post()
                .uri(ACCOMMODATION_URI)
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_HOST_ID_5)
                .bodyValue(accommodationDto)
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void hostGetAllOfHisAccommodations() {
        AccommodationDtoCreate accommodationDto = createAccommodation();

        Long accommodationId = postAccommodation(accommodationDto);

        accommodationDto.setId(accommodationId);

        AccommodationDtoCreate accommodationDto2 = AccommodationDtoCreate.builder()
                .name(updatedName)
                .description("Nice cottage in outskirts")
                .accommodationType(AccommodationType.COTTAGE)
                .build();

        Long accommodationId2 = webTestClient.post()
                .uri(ACCOMMODATION_URI)
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_HOST_ID_6)
                .bodyValue(accommodationDto2)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Long.class)
                .getResponseBody().toStream().findAny().get();

        accommodationDto2.setId(accommodationId2);

        List<AccommodationDto> result
                = webTestClient.get()
                .uri(ACCOMMODATION_URI + "/my-accommodations")
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_HOST_ID_5)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AccommodationDto>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getName(), name);

        List<AccommodationDto> result2
                = webTestClient.get()
                .uri(ACCOMMODATION_URI + "/my-accommodations")
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_HOST_ID_6)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AccommodationDto>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(result2);
        assertEquals(result2.size(), 1);
        assertEquals(result2.get(0).getName(), updatedName);
        Long idFromDatabase = result
                .stream()
                .filter(value -> value.getName().equals(accommodationDto.getName()))
                .map(AccommodationDto::getId)
                .findAny()
                .orElseThrow();

        assertEquals(idFromDatabase, accommodationId);

        AccommodationDto accommodationDtoResult = getAccommodation(idFromDatabase);

        assertEquals(accommodationDtoResult.getName(), accommodationDto.getName());
    }

    @Test
    void updateAccommodation() {
        AccommodationDtoCreate accommodationDto = createAccommodation();

        Long id = postAccommodation(accommodationDto);

        accommodationDto.setId(id);

        List<AccommodationDto> result
                = webTestClient.get()
                .uri(ACCOMMODATION_URI + "?page=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AccommodationDto>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(result);


        MQTransferObject<Object> object = null;
        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Accommodation accommodation = messageMapper.accommodationMessageDtoToEntity(
                rabbitListener.hashMapToAccommodation((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Accommodation");
        assertEquals(accommodation, mapper.domainToEntity(mapper.dtoToDomain(result.get(0))));

        AccommodationDtoUpdate accommodationDtoUpdated = AccommodationDtoUpdate.builder()
                .id(id)
                .name(updatedName)
                .description("Nice hotel close to city center")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        webTestClient.put()
                .uri(ACCOMMODATION_URI)
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_HOST_ID_5)
                .bodyValue(accommodationDtoUpdated)
                .exchange()
                .expectStatus().isOk();


        AccommodationDto accommodationDtoResult = getAccommodation(id);

        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        accommodation = messageMapper.accommodationMessageDtoToEntity(
                rabbitListener.hashMapToAccommodation((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "UPDATE");
        assertEquals(object.getEntityType(), "Accommodation");
        assertEquals(accommodation, mapper.domainToEntity(mapper.dtoToDomain(accommodationDtoResult)));
        assertEquals(accommodationDtoResult.getName(), accommodationDtoUpdated.getName());
    }

    @Test
    void updateAccommodation_HostIsNotOwnerOfAccommodation() {
        AccommodationDtoCreate accommodationDto = createAccommodation();

        Long id = postAccommodation(accommodationDto);

        accommodationDto.setId(id);

        List<AccommodationDto> result
                = webTestClient.get()
                .uri(ACCOMMODATION_URI + "?page=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AccommodationDto>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(result);


        MQTransferObject<Object> object = null;
        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Accommodation accommodation = messageMapper.accommodationMessageDtoToEntity(
                rabbitListener.hashMapToAccommodation((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Accommodation");
        assertEquals(accommodation, mapper.domainToEntity(mapper.dtoToDomain(result.get(0))));

        AccommodationDtoUpdate accommodationDtoUpdated = AccommodationDtoUpdate.builder()
                .id(id)
                .name(updatedName)
                .description("Nice hotel close to city center")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        webTestClient.put()
                .uri(ACCOMMODATION_URI)
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_HOST_ID_6)
                .bodyValue(accommodationDtoUpdated)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateAccommodationAndUnitsNotDeleted() {
        AccommodationDtoCreate accommodationDtoCreate = createAccommodation();

        Long accommodationId = postAccommodation(accommodationDtoCreate);

        AccommodationDto accommodationDto = getAccommodation(accommodationId);

        AccommodationUnitDto unit1 = AccommodationUnitDto.builder()
                .name("Unit 1")
                .accommodation(accommodationDto)
                .description("Nice cozy room")
                .capacity(3)
                .build();

        AccommodationUnitDto unit2 = AccommodationUnitDto.builder()
                .name("Unit 2")
                .accommodation(accommodationDto)
                .description("Room with ocean view")
                .capacity(2)
                .build();

        Long unitId1 = webTestClient.post()
                .uri("/rooms")
                .bodyValue(unit1)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Long.class)
                .getResponseBody().toStream().findAny().get();


        Long unitId2 = webTestClient.post()
                .uri("/rooms")
                .bodyValue(unit2)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Long.class)
                .getResponseBody().toStream().findAny().get();

        List<AccommodationUnitDto> units = webTestClient.get()
                .uri(ACCOMMODATION_URI + "/{accommodationId}/rooms", accommodationId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AccommodationUnitDto>() {
                })
                .returnResult().getResponseBody();

        assertThat(units.size()).isEqualTo(2);
        assertThat(units.stream().map(AccommodationUnitDto::getId).toList()).containsExactlyElementsOf(List.of(unitId1, unitId2));

        AccommodationDtoUpdate accommodationDtoUpdated = AccommodationDtoUpdate.builder()
                .id(accommodationId)
                .name(updatedName)
                .description("Nice hotel close to city center")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        webTestClient.put()
                .uri(ACCOMMODATION_URI)
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_HOST_ID_5)
                .bodyValue(accommodationDtoUpdated)
                .exchange()
                .expectStatus()
                .isOk();

        List<AccommodationUnitDto> unitsAfterUpdated = webTestClient.get()
                .uri(ACCOMMODATION_URI + "/{accommodationId}/rooms", accommodationId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AccommodationUnitDto>() {
                })
                .returnResult().getResponseBody();

        assertThat(units.size()).isEqualTo(2);
        assertThat(units.stream().map(AccommodationUnitDto::getId).toList()).containsExactlyElementsOf(List.of(unitId1, unitId2));
    }

    @Test
    void deleteAccommodation() {

        AmenityDto am = AmenityDto.builder()
                .amenity("Pool")
                .build();
        AmenityDto am2 = AmenityDto.builder()
                .amenity("Terrace")
                .build();
        List<AmenityDto> amenities = new ArrayList<>();
        amenities.add(am);
        amenities.add(am2);

        AccommodationDtoCreate accommodationDto = createAccommodation();

        accommodationDto.setAmenities(amenities);

        Long id = postAccommodation(accommodationDto);
        accommodationDto.setId(id);
        am.setId(1L);
        am2.setId(2L);
        List<AccommodationDto> result
                = webTestClient.get()
                .uri(ACCOMMODATION_URI + "?page=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AccommodationDto>() {
                })
                .returnResult()
                .getResponseBody();

        MQTransferObject<Object> object = null;
        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Accommodation accommodation = messageMapper.accommodationMessageDtoToEntity(
                rabbitListener.hashMapToAccommodation((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Accommodation");
        assertEquals(accommodation, mapper.domainToEntity(mapper.dtoToDomain(result.get(0))));
        Assertions.assertNotNull(result);

        webTestClient.delete()
                .uri(ACCOMMODATION_URI + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_HOST_ID_5)
                .exchange()
                .expectStatus().isOk();

            /*Listener receives from queue also messages regarding Insert Amenity which is
        implicitly called  through adding amenities to accommodation, so while therefore
        in list that is filled by listener - 0th place is taken by INSERT ACCOMMODATION
        1st place is taken by INSERT AMENITY1 and 2nd place is filled with INSERT AMENITY2
        so DELETE ACCOMMODATION will take 4th place in queue.
         */

        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        accommodation = messageMapper.accommodationMessageDtoToEntity(
                rabbitListener.hashMapToAccommodation((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "UPDATE");
        assertEquals(object.getEntityType(), "Accommodation");
        assertEquals(accommodation, mapper.domainToEntity(mapper.dtoToDomain(result.get(0))));

    }

    @Test
    void deleteAccommodation_wrongRole() {


        AccommodationDtoCreate accommodationDto = createAccommodation();


        Long id = postAccommodation(accommodationDto);
        accommodationDto.setId(id);
        List<AccommodationDto> result
                = webTestClient.get()
                .uri(ACCOMMODATION_URI + "?page=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AccommodationDto>() {
                })
                .returnResult()
                .getResponseBody();

        MQTransferObject<Object> object = null;
        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Accommodation accommodation = messageMapper.accommodationMessageDtoToEntity(
                rabbitListener.hashMapToAccommodation((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Accommodation");
        assertEquals(accommodation, mapper.domainToEntity(mapper.dtoToDomain(result.get(0))));
        Assertions.assertNotNull(result);

        webTestClient.delete()
                .uri(ACCOMMODATION_URI + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_USER_ID_5)
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void deleteAccommodation_userNotOwnerOfAccommodation() {


        AccommodationDtoCreate accommodationDto = createAccommodation();


        Long id = postAccommodation(accommodationDto);
        accommodationDto.setId(id);
        List<AccommodationDto> result
                = webTestClient.get()
                .uri(ACCOMMODATION_URI + "?page=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AccommodationDto>() {
                })
                .returnResult()
                .getResponseBody();

        MQTransferObject<Object> object = null;
        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Accommodation accommodation = messageMapper.accommodationMessageDtoToEntity(
                rabbitListener.hashMapToAccommodation((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Accommodation");
        assertEquals(accommodation, mapper.domainToEntity(mapper.dtoToDomain(result.get(0))));
        Assertions.assertNotNull(result);

        webTestClient.delete()
                .uri(ACCOMMODATION_URI + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_HOST_ID_6)
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void updateAccommodationThatDoesNotExist() {

        AccommodationDtoUpdate accommodationDtoUpdated = AccommodationDtoUpdate.builder()
                .id(1L)
                .name("Hilton BGD")
                .description("Nice hotel close to city center")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        webTestClient.put()
                .uri(ACCOMMODATION_URI)
                .header(HttpHeaders.AUTHORIZATION, JWT_ROLE_HOST_ID_5)
                .bodyValue(accommodationDtoUpdated)
                .exchange()
                .expectStatus().isNotFound();
    }


}