package rs.ac.bg.fon.reservationservice.eventListener.eventHandlers;

import rs.ac.bg.fon.reservationservice.dto.message.MQTransferObject;
import rs.ac.bg.fon.reservationservice.dto.message.ReservationEmailMessage;
import rs.ac.bg.fon.reservationservice.exceptions.MessageQueueException;
import rs.ac.bg.fon.reservationservice.mapper.ReservationMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter
@Setter
@Slf4j
@ConfigurationProperties
public class MessageQueueHandler {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ReservationMapper reservationMapper;

    @Value("${rabbitmq.exchanges.direct}")
    private String directExchange;

    @Value("${rabbitmq.exchanges.topic}")
    private String topicExchange;

    @Value("${rabbitmq.routing-keys.search-service}")
    private String searchRoutingKey;

    @Value("${rabbitmq.routing-keys.email-service}")
    private String emailRoutingKey;

    private Map<Class<?>, String> correspondingMapperForClass;

    public void sendMessage(Object object, String operation) {
        Class<?> cls = object.getClass();
        try {
            MQTransferObject<?> transferObject = new MQTransferObject<>(
                    operation,
                    cls.getSimpleName(),
                    reservationMapper.getClass().getMethod(correspondingMapperForClass.get(cls), cls).invoke(reservationMapper, object));

            rabbitTemplate.convertAndSend(directExchange, searchRoutingKey, transferObject);
        } catch (Exception e) {
            throw new MessageQueueException(e);
        }
    }

    public void sendMessageToEmailService(ReservationEmailMessage reservationEmailMessage) {
        rabbitTemplate.convertAndSend(topicExchange, emailRoutingKey, reservationEmailMessage);
    }
}