package rs.ac.bg.fon.searchservice.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import rs.ac.bg.fon.searchservice.config.MessageDtoConfig;
import rs.ac.bg.fon.searchservice.dto.message.IdMessageDto;
import rs.ac.bg.fon.searchservice.dto.message.MQTransferObject;
import rs.ac.bg.fon.searchservice.rabbitmq.eventHandlers.*;
import rs.ac.bg.fon.searchservice.repository.AccommodationRepository;
import rs.ac.bg.fon.searchservice.util.IdHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RabbitMQConsumer {

    private final ObjectMapper objectMapper;
    private final MessageDtoConfig messageDtoConfig;
    private final Map<Class<?>, DataChangedMessageEventHandler<?>> eventHandlers;
    private final AccommodationEventHandler accommodationEventHandler;
    private final AccommodationUnitEventHandler accommodationUnitEventHandler;
    private final AddressEventHandler addressEventHandler;
    private final PriceEventHandler priceEventHandler;


    public RabbitMQConsumer(ObjectMapper objectMapper,
                            MessageDtoConfig messageDtoConfig,
                            List<DataChangedMessageEventHandler<?>> handlers,
                            AccommodationEventHandler accommodationEventHandler,
                            AccommodationUnitEventHandler accommodationUnitEventHandler,
                            AddressEventHandler addressEventHandler,
                            PriceEventHandler priceEventHandler) {
        this.objectMapper = objectMapper;
        this.messageDtoConfig = messageDtoConfig;
        this.eventHandlers =
                handlers.stream()
                        .collect(Collectors
                                .toMap(DataChangedMessageEventHandler::getEntityType, Function.identity()));
        this.accommodationEventHandler = accommodationEventHandler;
        this.accommodationUnitEventHandler = accommodationUnitEventHandler;
        this.addressEventHandler = addressEventHandler;
        this.priceEventHandler = priceEventHandler;
    }

    @RabbitListener(queues = "#{queuesConfig.getQueuesProperties().correspondingQueueNameForService().values().stream().toList()}")
    public void onChange(MQTransferObject<Object> message) {

        Class<? extends IdHolder> dtoClass = messageDtoConfig.getCorrespondingMessageDtoClassForString().get(message.getEntityType());

        IdHolder messageDto = objectMapper.convertValue(message.getMessage(), dtoClass);

        DataChangedMessageEventHandler<?> handler = eventHandlers.get(dtoClass);

        switch (message.getEventType()) {

            case INSERT -> handler.handleInsertAndUpdate(messageDto);
//            case UPDATE -> handler.handleUpdate(messageDto);
//            case DELETE -> handler.handleDelete(messageDto.getId());

        }

    }

    @RabbitListener(queues = "${rabbitmq.notify.delete.accommodation.queue}")
    public void accommodationDeleted(IdMessageDto accommodation){
        log.info("Consumed accommodation with id: {} from queue", accommodation.getId());
        accommodationEventHandler.handleDelete(accommodation);
    }

    @RabbitListener(queues = "${rabbitmq.notify.delete.accommodation-unit.queue}")
    public void accommodationUnitDeleted(IdMessageDto accommodationUnit){
        log.info("Consumed accommodation unit with id: {} from queue", accommodationUnit.getId());
        accommodationUnitEventHandler.handleDelete(accommodationUnit);
    }

    @RabbitListener(queues = "${rabbitmq.notify.delete.price.queue}")
    public void priceTransactionDeleted(IdMessageDto price){
        log.info("Consumed price with id: {} from queue", price.getId());
        priceEventHandler.handleDelete(price);
    }

    @RabbitListener(queues = "${rabbitmq.notify.delete.address.queue}")
    public void addressTransactionDeleted(IdMessageDto address){
        log.info("Consumed address with id: {} from queue", address.getId());
        addressEventHandler.handleDelete(address);
    }


}
