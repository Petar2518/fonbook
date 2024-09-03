package rs.ac.bg.fon.accommodationservice.eventListener.eventHandlers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class DeleteHandler {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.orchestrator.delete.exchange}")
    private String exchangeType;
    @Value("${rabbitmq.orchestrator.delete.routing-key}")
    private String routingKey;

    public void sendMessage(){}
}
