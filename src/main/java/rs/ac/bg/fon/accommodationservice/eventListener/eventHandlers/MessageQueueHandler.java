package rs.ac.bg.fon.accommodationservice.eventListener.eventHandlers;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.dto.message.MQTransferObject;
import rs.ac.bg.fon.accommodationservice.exception.specific.MessageQueueException;
import rs.ac.bg.fon.accommodationservice.mapper.MessageMapper;

import java.util.Map;

@Component
@Getter
@Setter
@ConfigurationProperties
@Slf4j
public class MessageQueueHandler {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MessageMapper mapper;

    @Value("${rabbitmq.crud-operations.direct-exchange.name}")
    private String exchangeType;
    @Value("${rabbitmq.crud-operations.direct-exchange.routing-key}")
    private String routingKey;

    private Map<Class<?>, String> correspondingMapperForClass;

    public void sendMessage(Object o, String operation) {
        String className = o.getClass().getSimpleName();
        Class<?> cls = o.getClass();
        try {
            if (correspondingMapperForClass.containsKey(cls)) {
                MQTransferObject<?> transferObject = new MQTransferObject<>(
                        operation,
                        className,
                        mapper.getClass().getMethod(correspondingMapperForClass.get(cls), cls).invoke(mapper, o));

                rabbitTemplate.convertAndSend(exchangeType, routingKey, transferObject);
            } else {
                log.warn("{} is not defined correctly in 'application.yaml' file for message queue",cls);
            }
        } catch (Exception e) {
            throw new MessageQueueException(e);
        }
    }
}
