package rs.ac.bg.fon.orchestratorservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.SerializationUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccommodationOrchestrator {

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Value("${rabbitmq.revert.delete.exchange}")
    private String deleteFailureExchangeType;
    @Value("${rabbitmq.confirm.delete.exchange}")
    private String deleteSuccessExchangeType;
    @Value("${rabbitmq.notify.delete.exchange}")
    private String notifyDeleteExchangeType;
    @Value("${rabbitmq.notify.delete.accommodation.routing-key}")
    private String notifyDeleteRoutingKey;
    @Value("${rabbitmq.revert.delete.accommodation.routing-key}")
    private String deleteFailureRoutingKey;
    @Value("${rabbitmq.confirm.delete.accommodation.routing-key}")
    private String deleteSuccessRoutingKey;


    @RabbitListener(queues = "${rabbitmq.start.delete.accommodation.queue}")
    public void accommodationStartTransaction(Object accommodationId){

        log.info("Consumed message accommodation with id: {} from queue while beginning transaction", accommodationId);
        rabbitTemplate.convertAndSend(notifyDeleteExchangeType, notifyDeleteRoutingKey, accommodationId);
    }

    @RabbitListener(queues = "${rabbitmq.failure.delete.accommodation.queue}")
    public void accommodationTransactionFailed(Object accommodationId){
        log.info("Consumed message accommodation with id: {} from queue while failing transaction", accommodationId);
        rabbitTemplate.convertAndSend(deleteFailureExchangeType, deleteFailureRoutingKey, accommodationId);
    }

    @RabbitListener(queues = "${rabbitmq.success.delete.accommodation.queue}")
    public void accommodationTransactionSucceeded(Object accommodationId){
        log.info("Consumed message accommodation with id: {} from queue while succeeding transaction", accommodationId);
        rabbitTemplate.convertAndSend(deleteSuccessExchangeType, deleteSuccessRoutingKey, accommodationId);
    }

}
