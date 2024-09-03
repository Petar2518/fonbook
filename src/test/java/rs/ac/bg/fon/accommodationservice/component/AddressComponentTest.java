package rs.ac.bg.fon.accommodationservice.component;

import rs.ac.bg.fon.accommodationservice.domain.AddressDomain;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationDto;
import rs.ac.bg.fon.accommodationservice.dto.AddressDto;
import rs.ac.bg.fon.accommodationservice.dto.create.AccommodationDtoCreate;
import rs.ac.bg.fon.accommodationservice.dto.message.MQTransferObject;
import rs.ac.bg.fon.accommodationservice.dto.update.AddressDtoUpdate;
import rs.ac.bg.fon.accommodationservice.mapper.AddressMapper;
import rs.ac.bg.fon.accommodationservice.mapper.MessageMapper;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.Address;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("springboot")
public class AddressComponentTest extends ComponentTestBase {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    AddressMapper mapper;

    private final String ADDRESS_URI = "/addresses";

    String name = "Hilton Belgrade";

    @Autowired
    RabbitListenerTestComponent rabbitListener;

    String streetUpdated = "Bulevar Milutina Milankovica";
    String streetNumberUpdated = "1";

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

    private AddressDto createAddress(AccommodationDto accommodationDto) {
        return AddressDto.builder()
                .id(accommodationDto.getId())
                .accommodation(accommodationDto)
                .country("Serbia")
                .city("Belgrade")
                .postalCode("11000")
                .street("Bulevar Kralja Aleksandra")
                .streetNumber("113a")
                .build();
    }

    private Long postAddress(AddressDto addressDto) {
        return webTestClient.post()
                .uri(ADDRESS_URI)
                .bodyValue(addressDto)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Long.class)
                .getResponseBody().toStream().findAny().get();
    }

    private AddressDto getAddress(Long id) {
        return webTestClient.get()
                .uri(ADDRESS_URI + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressDto.class)
                .returnResult().getResponseBody();
    }

    @Test
    void addAddress() {
        AccommodationDtoCreate accommodationDto = createAccommodation();

        Long id = postAccommodation(accommodationDto);

        accommodationDto.setId(id);

        AccommodationDto accommodationDtoResult = getAccommodation(id);

        AddressDto addressDto = createAddress(accommodationDtoResult);

        Long addressId = postAddress(addressDto);

        MQTransferObject<Object> object = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Address address = messageMapper.addressMessageDtoToEntity(
                rabbitListener.hashMapToAddress((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Address");
        assertEquals(address, mapper.domainToEntity(mapper.dtoToDomain(addressDto)));


        List<AddressDto> result
                = webTestClient.get()
                .uri(ADDRESS_URI + "/" + addressId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AddressDto>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(result).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id","accommodation").contains(addressDto);
    }

    @Test
    void addAddressValuesNull() {

        AccommodationDtoCreate accommodationDto = AccommodationDtoCreate.builder()
                .name(name)
                .description("Nice hotel in city center")
                .accommodationType(AccommodationType.HOTEL)
                .build();

        Long id = postAccommodation(accommodationDto);

        AccommodationDto accommodationDtoResult = webTestClient.get()
                .uri("/accommodations/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccommodationDto.class)
                .returnResult()
                .getResponseBody();

        AddressDto addressDto = AddressDto.builder()
                .id(id)
                .accommodation(accommodationDtoResult)
                .country("Serbia")
                .city("Belgrade")
                .postalCode("11000")
                .streetNumber("113a")
                .build();

        webTestClient.post()
                .uri(ADDRESS_URI)
                .bodyValue(addressDto)
                .exchange()
                .expectStatus().isBadRequest();


    }

    @Test
    void updateAddress() {
        AccommodationDtoCreate accommodationDto = createAccommodation();

        Long id = postAccommodation(accommodationDto);

        AccommodationDto accommodationDtoResult = getAccommodation(id);

        AddressDto addressDto = createAddress(accommodationDtoResult);

        Long addressId = postAddress(addressDto);


        MQTransferObject<Object> object = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Address address = messageMapper.addressMessageDtoToEntity(
                rabbitListener.hashMapToAddress((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Address");
        assertEquals(address, mapper.domainToEntity(mapper.dtoToDomain(addressDto)));

        List<AddressDto> result
                = webTestClient.get()
                .uri(ADDRESS_URI + "/" + addressId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AddressDto>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(result);

        AddressDtoUpdate addressDtoUpdated = AddressDtoUpdate.builder()
                .id(addressId)
                .postalCode("11000")
                .street(streetUpdated)
                .streetNumber(streetNumberUpdated)
                .build();

        webTestClient.put()
                .uri(ADDRESS_URI)
                .bodyValue(addressDtoUpdated)
                .exchange()
                .expectStatus().isOk();

        object = null;
        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



        address = messageMapper.addressMessageDtoToEntity(
                rabbitListener.hashMapToAddress((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "UPDATE");
        assertEquals(object.getEntityType(), "Address");

        AddressDto addressDtoResult = getAddress(addressId);
        assertEquals(address, mapper.domainToEntity(mapper.dtoToDomain(addressDtoResult)));


        assertEquals(addressDtoResult.getStreet(), addressDtoUpdated.getStreet());
        assertEquals(addressDtoResult.getStreetNumber(), addressDtoUpdated.getStreetNumber());

        assertEquals(id, addressId);

    }

    @Test
    void deleteAddress() {
        AccommodationDtoCreate accommodationDto = createAccommodation();

        Long id = postAccommodation(accommodationDto);

        AccommodationDto accommodationDtoResult = getAccommodation(id);

        AddressDto addressDto = createAddress(accommodationDtoResult);

        AddressDomain addressDomain = mapper.dtoToDomain(addressDto);
        addressDomain.setDeleted(false);
        addressDomain.setAccommodation(null);

        Long addressId = postAddress(addressDto);

        MQTransferObject<Object> object = null;
        try {
            rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Address address = messageMapper.addressMessageDtoToEntity(
                rabbitListener.hashMapToAddress((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "INSERT");
        assertEquals(object.getEntityType(), "Address");
        assertEquals(address, mapper.domainToEntity(mapper.dtoToDomain(addressDto)));

        List<AddressDto> result
                = webTestClient.get()
                .uri(ADDRESS_URI + "/" + addressId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<AddressDto>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(result);

        webTestClient.delete()
                .uri(ADDRESS_URI + "/{id}", addressId)
                .exchange()
                .expectStatus().isOk();

        try {
            object = rabbitListener.getMqObject().poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        address = messageMapper.addressMessageDtoToEntity(
                rabbitListener.hashMapToAddress((LinkedHashMap<?, ?>) object.getMessage()));
        assertEquals(object.getEventType(), "UPDATE");
        assertEquals(object.getEntityType(), "Address");
        assertEquals(address, mapper.domainToEntity(addressDomain));

    }

    @Test
    void updateAddressThatDoesNotExist() {

        AccommodationDtoCreate accommodationDtoUpdated = createAccommodation();
        Long id = postAccommodation(accommodationDtoUpdated);

        accommodationDtoUpdated.setId(id);

        AddressDtoUpdate addressDto = AddressDtoUpdate.builder()
                .id(0L)
                .postalCode("11000")
                .street("Bulevar Kralja Aleksandra")
                .streetNumber("113a")
                .build();

        webTestClient.put()
                .uri(ADDRESS_URI)
                .bodyValue(addressDto)
                .exchange()
                .expectStatus().isNotFound();
    }
}
