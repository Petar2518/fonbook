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
public class PriceOrchestrator {

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Value("${rabbitmq.revert.delete.exchange}")
    private String deleteFailureExchangeType;
    @Value("${rabbitmq.confirm.delete.exchange}")
    private String deleteSuccessExchangeType;
    @Value("${rabbitmq.notify.delete.exchange}")
    private String notifyDeleteExchangeType;
    @Value("${rabbitmq.notify.delete.price.routing-key}")
    private String notifyDeleteRoutingKey;
    @Value("${rabbitmq.revert.delete.price.routing-key}")
    private String deleteFailureRoutingKey;
    @Value("${rabbitmq.confirm.delete.price.routing-key}")
    private String deleteSuccessRoutingKey;

    @RabbitListener(queues = "${rabbitmq.failure.delete.price.queue}")
    public void priceTransactionFailed(Object priceId){
        log.info("Consumed message price with id: {} from queue while failing transaction", ((Message) priceId).getBody());
        rabbitTemplate.convertAndSend(deleteFailureExchangeType, deleteFailureRoutingKey, priceId);
    }

    @RabbitListener(queues = "${rabbitmq.start.delete.price.queue}")
    public void priceStartTransaction(Object priceId){
        log.info("Consumed message price with id: {} from queue while beginning transaction", ((Message) priceId).getBody());
        rabbitTemplate.convertAndSend(notifyDeleteExchangeType, notifyDeleteRoutingKey, priceId);
    }

    @RabbitListener(queues = "${rabbitmq.success.delete.price.queue}")
    public void priceTransactionSucceeded(Object priceId){
        log.info("Consumed message price with id: {} from queue while succeeding transaction", ((Message) priceId).getBody());
        rabbitTemplate.convertAndSend(deleteSuccessExchangeType, deleteSuccessRoutingKey, priceId);
    }



}
