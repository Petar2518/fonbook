package rs.ac.bg.fon.searchservice.config;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@Getter
@Setter
public class QueuesConfig {

    private final QueuesPropertiesConfig queuesProperties;

    private final ConnectionFactory connectionFactory;

    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory);
    }

    @Value("${rabbitmq.notify.delete.exchange}")
    private String notifyDeleteExchange;


    @Value("${rabbitmq.notify.delete.accommodation.queue}")
    private String accommodationNotifyDeleteQueue;

    @Value("${rabbitmq.notify.delete.accommodation.routing-key}")
    private String accommodationNotifyDeleteRoutingKey;


    @Value("${rabbitmq.notify.delete.accommodation-unit.queue}")
    private String accommodationUnitNotifyDeleteQueue;

    @Value("${rabbitmq.notify.delete.accommodation-unit.routing-key}")
    private String accommodationUnitNotifyDeleteRoutingKey;


    @Value("${rabbitmq.notify.delete.address.queue}")
    private String addressNotifyDeleteQueue;

    @Value("${rabbitmq.notify.delete.address.routing-key}")
    private String addressNotifyDeleteRoutingKey;


    @Value("${rabbitmq.notify.delete.price.queue}")
    private String priceNotifyDeleteQueue;

    @Value("${rabbitmq.notify.delete.price.routing-key}")
    private String priceNotifyDeleteRoutingKey;

    @Bean
    public Queue accommodationQueue() {
        return new Queue(queuesProperties.correspondingQueueNameForService().get("accommodation"), true);
    }

    @Bean
    public Queue reservationQueue() {
        return new Queue(queuesProperties.correspondingQueueNameForService().get("reservation"), true);
    }

    @Bean
    public DirectExchange accommodationDirectExchange() {
        return new DirectExchange("Accommodation-CRUD-Exchange");
    }

    @Bean
    public DirectExchange reservationDirectExchange() {
        return new DirectExchange("Reservation-CRUD-Exchange");
    }

    @Bean
    public TopicExchange notifyDeleteExchange() {
        return new TopicExchange(notifyDeleteExchange);
    }

    @Bean
    public Queue accommodationNotifyDeleteQueue() {
        return new Queue(accommodationNotifyDeleteQueue, true);
    }

    @Bean
    public Queue accommodationUnitNotifyDeleteQueue() {
        return new Queue(accommodationUnitNotifyDeleteQueue, true);
    }


    @Bean
    public Binding accommodationNotifyBinding() {
        return BindingBuilder
                .bind(accommodationNotifyDeleteQueue())
                .to(notifyDeleteExchange())
                .with(accommodationNotifyDeleteRoutingKey);
    }

    @Bean
    public Binding accommodationUnitNotifyBinding() {
        return BindingBuilder
                .bind(accommodationUnitNotifyDeleteQueue())
                .to(notifyDeleteExchange())
                .with(accommodationUnitNotifyDeleteRoutingKey);
    }

    @Bean
    public Binding priceNotifyBinding() {
        return BindingBuilder
                .bind(priceNotifyDeleteQueue())
                .to(notifyDeleteExchange())
                .with(priceNotifyDeleteRoutingKey);
    }

    @Bean
    public Binding addressNotifyBinding() {
        return BindingBuilder
                .bind(addressNotifyDeleteQueue())
                .to(notifyDeleteExchange())
                .with(addressNotifyDeleteRoutingKey);
    }

    @Bean
    public Queue priceNotifyDeleteQueue() {
        return new Queue(priceNotifyDeleteQueue, true);
    }

    @Bean
    public Queue addressNotifyDeleteQueue() {
        return new Queue(addressNotifyDeleteQueue, true);
    }

    @Bean
    public Binding accommodationBinding() {
        return BindingBuilder
                .bind(accommodationQueue())
                .to(accommodationDirectExchange())
                .with("accommodation.details.change");
    }

    @Bean
    public Binding reservationBinding() {
        return BindingBuilder
                .bind(reservationQueue())
                .to(reservationDirectExchange())
                .with("reservation.details.change");
    }


    @Bean
    public MessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
