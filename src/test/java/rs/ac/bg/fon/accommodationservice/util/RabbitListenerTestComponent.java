package rs.ac.bg.fon.accommodationservice.util;

import org.springframework.beans.factory.annotation.Qualifier;
import rs.ac.bg.fon.accommodationservice.dto.message.*;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import org.junit.jupiter.api.Tag;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Tag("springboot")
@TestComponent
public class RabbitListenerTestComponent {

    private static final String SEARCH_QUEUE_NAME = "Search";

    @Bean(name="main")
    public Queue searchQueue() {
        return new Queue(SEARCH_QUEUE_NAME, true);
    }


    @Value("${rabbitmq.crud-operations.direct-exchange.routing-key}")
    private String SEARCH_ROUTING_KEY;

    @Bean(name= "tests")
    public Binding binding(@Qualifier("main") Queue queue, DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(SEARCH_ROUTING_KEY);
    }

    public AccommodationMessageDto hashMapToAccommodation(LinkedHashMap<?, ?> object) {
        List<LinkedHashMap<?, ?>> amenities = (ArrayList<LinkedHashMap<?, ?>>) object.get("amenities");
        List<AmenityMessageDto> amenities1 = new ArrayList<>();
        if (amenities != null) {
            amenities1 = amenities.stream().map(this::hashMapToAmenity).toList();
        }

        return AccommodationMessageDto.builder()
                .id(((Integer) object.get("id")).longValue())
                .accommodationType(AccommodationType.valueOf((String) object.get("accommodationType")))
                .description((String) object.get("description"))
                .hostId(((Integer) object.get("hostId")).longValue())
                .name((String) object.get("name"))
                .amenities(amenities1)
                .build();

    }

    public AmenityMessageDto hashMapToAmenity(LinkedHashMap<?, ?> object) {
        return AmenityMessageDto.builder()
                .id(((Integer) object.get("id")).longValue())
                .amenity((String) object.get("amenity"))
                .build();
    }

    public AccommodationUnitMessageDto hashMapToUnit(LinkedHashMap<?, ?> object) {
        LinkedHashMap<?, ?> accommodation = (LinkedHashMap<?, ?>) object.get("accommodation");
        AccommodationMessageDto accommodation1 = null;
        if (accommodation != null) {
            accommodation1 = hashMapToAccommodation(accommodation);

        }
        return AccommodationUnitMessageDto.builder()
                .id(((Integer) object.get("id")).longValue())
                .name((String) object.get("name"))
                .capacity((Integer) object.get("capacity"))
                .description((String) object.get("description"))
                .accommodation(accommodation1)
                .build();
    }

    public PriceMessageDto hashMapToPrice(LinkedHashMap<?, ?> object) {
        LinkedHashMap<?, ?> unit = (LinkedHashMap<?, ?>) object.get("accommodationUnit");
        AccommodationUnitMessageDto accommodationUnit = null;
        if (unit != null) {
            accommodationUnit = hashMapToUnit(unit);
        }
        ArrayList<?> date = ((ArrayList<?>) object.get("dateTo"));
        LocalDate endingDate = LocalDate.of((Integer) date.get(0), (Integer) date.get(1), (Integer) date.get(2));

        date = ((ArrayList<?>) object.get("dateFrom"));
        LocalDate startingDate = LocalDate.of((Integer) date.get(0), (Integer) date.get(1), (Integer) date.get(2));


        return PriceMessageDto.builder()
                .id(((Integer) object.get("id")).longValue())
                .amount(BigDecimal.valueOf((Double) object.get("amount")))
                .accommodationUnit(accommodationUnit)
                .dateFrom(startingDate)
                .dateTo(endingDate)
                .currency((String) object.get("currency"))
                .build();
    }

    public AddressMessageDto hashMapToAddress(LinkedHashMap<?, ?> object) {

        LinkedHashMap<?, ?> accommodation = (LinkedHashMap<?, ?>) object.get("accommodation");
        AccommodationMessageDto accommodation1 = null;
        if (accommodation != null) {
            accommodation1 = hashMapToAccommodation(accommodation);
            if (accommodation1.getAmenities() == null) {
                accommodation1.setAmenities(new ArrayList<>());
            }
        }
        return AddressMessageDto.builder()
                .id(((Integer) object.get("id")).longValue())
                .country((String) object.get("country"))
                .city((String) object.get("city"))
                .postalCode((String) object.get("postalCode"))
                .street((String) object.get("street"))
                .streetNumber((String) object.get("streetNumber"))
                .latitude((String) object.get("latitude"))
                .longitude((String) object.get("longitude"))
                .accommodation(accommodation1)
                .build();
    }

    BlockingQueue<MQTransferObject<Object>> mqObject = new LinkedBlockingQueue<>();

    public void clearList() {
        mqObject = new LinkedBlockingQueue<>();
    }

    @RabbitListener(queues = {"Search"})
    public void onChange(MQTransferObject<Object> message) {
        mqObject.add(message);
    }

    public BlockingQueue<MQTransferObject<Object>> getMqObject() {
        return mqObject;
    }
}
