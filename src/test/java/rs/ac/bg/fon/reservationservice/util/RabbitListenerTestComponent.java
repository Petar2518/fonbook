package rs.ac.bg.fon.reservationservice.util;

import rs.ac.bg.fon.reservationservice.dto.ReservationDto;
import rs.ac.bg.fon.reservationservice.dto.message.MQTransferObject;
import rs.ac.bg.fon.reservationservice.model.DateRange;
import rs.ac.bg.fon.reservationservice.model.ReservationStatus;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@TestComponent
public class RabbitListenerTestComponent {


    private static final String SEARCH_QUEUE_NAME = "Search";

    @Bean
    public Queue searchQueue() {
        return new Queue(SEARCH_QUEUE_NAME, true);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(SEARCH_ROUTING_KEY);
    }

    @Value("${rabbitmq.routing-keys.search-service}")
    private String SEARCH_ROUTING_KEY;

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

    public ReservationDto hashMapToReservationDto(LinkedHashMap<?, ?> object) {

        LinkedHashMap<?, ?> dateRangeList = (LinkedHashMap<?, ?>) object.get("dateRange");


        DateRange dateRange = DateRange.builder()
                .checkInDate(arrayListToLocalDate((List<Integer>) dateRangeList.get("checkInDate")))
                .checkOutDate(arrayListToLocalDate((List<Integer>) dateRangeList.get("checkOutDate")))
                .build();

        return ReservationDto.builder()
                .id(((Integer) object.get("id")).longValue())
                .creationDate(arrayListToLocalDate((List<Integer>) object.get("creationDate")))
                .totalAmount(BigDecimal.valueOf((Integer) object.get("totalAmount")))
                .status(ReservationStatus.valueOf((String) object.get("status")))
                .currency((String) object.get("currency"))
                .dateRange(dateRange)
                .numberOfPeople((Integer) object.get("numberOfPeople"))
                .profileId(((Integer) object.get("profileId")).longValue())
                .accommodationUnitId(((Integer) object.get("accommodationUnitId")).longValue())
                .build();

    }

    public static LocalDate arrayListToLocalDate(List<Integer> dateValues) {
        if (dateValues.size() != 3) {
            throw new IllegalArgumentException("Date values list must contain year, month, and day");
        }

        int year = dateValues.get(0);
        int month = dateValues.get(1);
        int day = dateValues.get(2);

        return LocalDate.of(year, month, day);
    }


}
