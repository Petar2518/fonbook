package rs.ac.bg.fon.orchestratorservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccommodationUnitOrchestrator {

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Value("${rabbitmq.revert.delete.exchange}")
    private String deleteFailureExchangeType;
    @Value("${rabbitmq.confirm.delete.exchange}")
    private String deleteSuccessExchangeType;
    @Value("${rabbitmq.notify.delete.exchange}")
    private String notifyDeleteExchangeType;
    @Value("${rabbitmq.revert.delete.accommodationUnit.routing-key}")
    private String deleteFailureRoutingKey;
    @Value("${rabbitmq.confirm.delete.accommodationUnit.routing-key}")
    private String deleteSuccessRoutingKey;
    @Value("${rabbitmq.notify.delete.accommodationUnit.routing-key}")
    private String notifyDeleteRoutingKey;

    @RabbitListener(queues = "${rabbitmq.failure.delete.accommodation-unit.queue}")
    public void accommodationUnitTransactionFailed(Object accommodationUnitId){
        log.info("Consumed message accommodation unit with id: {} from queue while failing transaction", ((Message) accommodationUnitId).getBody());
        rabbitTemplate.convertAndSend(deleteFailureExchangeType, deleteFailureRoutingKey, accommodationUnitId);
    }

    @RabbitListener(queues = "${rabbitmq.start.delete.accommodation-unit.queue}")
    public void accommodationUnitStartTransaction(Object accommodationUnitId){
        log.info("Consumed message accommodation unit with id: {} from queue while beginning transaction", ((Message) accommodationUnitId).getBody());
        rabbitTemplate.convertAndSend(notifyDeleteExchangeType, notifyDeleteRoutingKey, accommodationUnitId);
    }

    @RabbitListener(queues = "${rabbitmq.failure.delete.accommodation-unit.queue}")
    public void accommodationUnitTransactionSucceeded(Object accommodationUnitId){
        log.info("Consumed message accommodation unit unit with id: {} from queue while succeeding transaction", ((Message) accommodationUnitId).getBody());
        rabbitTemplate.convertAndSend(deleteFailureExchangeType, deleteFailureRoutingKey, accommodationUnitId);
    }
}
