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
public class AddressOrchestrator {

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Value("${rabbitmq.revert.delete.exchange}")
    private String deleteFailureExchangeType;
    @Value("${rabbitmq.confirm.delete.exchange}")
    private String deleteSuccessExchangeType;
    @Value("${rabbitmq.notify.delete.exchange}")
    private String notifyDeleteExchangeType;
    @Value("${rabbitmq.notify.delete.address.routing-key}")
    private String notifyDeleteRoutingKey;
    @Value("${rabbitmq.revert.delete.address.routing-key}")
    private String deleteFailureRoutingKey;
    @Value("${rabbitmq.confirm.delete.address.routing-key}")
    private String deleteSuccessRoutingKey;

    @RabbitListener(queues = "${rabbitmq.failure.delete.address.queue}")
    public void addressTransactionFailed(Object addressId){
        log.info("Consumed message address with id: {} from queue while failing transaction", ((Message) addressId).getBody());
        rabbitTemplate.convertAndSend(deleteFailureExchangeType, deleteFailureRoutingKey, addressId);
    }

    @RabbitListener(queues = "${rabbitmq.start.delete.address.queue}")
    public void addressStartTransaction(Object addressId){
        log.info("Consumed message address with id: {} from queue while beginning transaction", ((Message) addressId).getBody());
        rabbitTemplate.convertAndSend(notifyDeleteExchangeType, notifyDeleteRoutingKey, addressId);
    }

    @RabbitListener(queues = "${rabbitmq.success.delete.address.queue}")
    public void addressTransactionSucceeded(Object addressId){
        log.info("Consumed message address with id: {} from queue while succeeding transaction", ((Message) addressId).getBody());
        rabbitTemplate.convertAndSend(deleteSuccessExchangeType, deleteSuccessRoutingKey, addressId);
    }
}
