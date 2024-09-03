package rs.ac.bg.fon.accommodationservice.config;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
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
public class RabbitMQConfig {


    private final ConnectionFactory connectionFactory;

    @Value("${rabbitmq.failure.insert.queues.accommodation}")
    private String accommodationFailureInsertQueue;

    @Value("${rabbitmq.failure.insert.exchanges}")
    private String failureInsertExchange;

    @Value("${rabbitmq.failure.insert.routing-keys.accommodation}")
    private String accommodationFailureInsertRoutingKey;

    @Value("${rabbitmq.failure.insert.queues.accommodation-unit}")
    private String accommodationUnitFailureInsertQueue;


    @Value("${rabbitmq.failure.insert.routing-keys.accommodation-unit}")
    private String accommodationUnitFailureInsertRoutingKey;

    @Value("${rabbitmq.failure.insert.queues.address}")
    private String addressFailureInsertQueue;


    @Value("${rabbitmq.failure.insert.routing-keys.address}")
    private String addressFailureInsertRoutingKey;

    @Value("${rabbitmq.failure.insert.queues.price}")
    private String priceFailureInsertQueue;


    @Value("${rabbitmq.failure.insert.routing-keys.price}")
    private String priceFailureInsertRoutingKey;



    @Value("${rabbitmq.success.delete.queues.accommodation}")
    private String accommodationSuccessDeleteQueue;

    @Value("${rabbitmq.success.delete.exchanges}")
    private String successDeleteExchange;

    @Value("${rabbitmq.success.delete.routing-keys.accommodation}")
    private String accommodationSuccessDeleteRoutingKey;

    @Value("${rabbitmq.success.delete.queues.accommodation-unit}")
    private String accommodationUnitSuccessDeleteQueue;


    @Value("${rabbitmq.success.delete.routing-keys.accommodation-unit}")
    private String accommodationUnitSuccessDeleteRoutingKey;

    @Value("${rabbitmq.success.delete.queues.address}")
    private String addressSuccessDeleteQueue;


    @Value("${rabbitmq.success.delete.routing-keys.address}")
    private String addressSuccessDeleteRoutingKey;

    @Value("${rabbitmq.success.delete.queues.price}")
    private String priceSuccessDeleteQueue;


    @Value("${rabbitmq.success.delete.routing-keys.price}")
    private String priceSuccessDeleteRoutingKey;


    @Value("${rabbitmq.failure.delete.queues.accommodation}")
    private String accommodationFailureDeleteQueue;

    @Value("${rabbitmq.failure.delete.exchanges}")
    private String failureDeleteExchange;

    @Value("${rabbitmq.failure.delete.routing-keys.accommodation}")
    private String accommodationFailureDeleteRoutingKey;

    @Value("${rabbitmq.failure.delete.queues.accommodation-unit}")
    private String accommodationUnitFailureDeleteQueue;


    @Value("${rabbitmq.failure.delete.routing-keys.accommodation-unit}")
    private String accommodationUnitFailureDeleteRoutingKey;

    @Value("${rabbitmq.failure.delete.queues.address}")
    private String addressFailureDeleteQueue;


    @Value("${rabbitmq.failure.delete.routing-keys.address}")
    private String addressFailureDeleteRoutingKey;

    @Value("${rabbitmq.failure.delete.queues.price}")
    private String priceFailureDeleteQueue;


    @Value("${rabbitmq.failure.delete.routing-keys.price}")
    private String priceFailureDeleteRoutingKey;


    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public TopicExchange failureExchange() {
        return new TopicExchange(failureInsertExchange);
    }

    @Bean
    public Queue accommodationFailureQueue() {
        return new Queue(accommodationFailureInsertQueue, true);
    }

    @Bean
    public Queue accommodationUnitFailureQueue() {
        return new Queue(accommodationUnitFailureInsertQueue, true);
    }

    @Bean
    public Queue priceFailureQueue() {
        return new Queue(priceFailureInsertQueue, true);
    }

    @Bean
    public Queue addressFailureQueue() {
        return new Queue(addressFailureInsertQueue, true);
    }


    @Bean
    public TopicExchange failureDeleteExchange() {
        return new TopicExchange(failureDeleteExchange);
    }

    @Bean
    public Queue accommodationFailureDeleteQueue() {
        return new Queue(accommodationFailureDeleteQueue, true);
    }

    @Bean
    public Queue accommodationUnitFailureDeleteQueue() {
        return new Queue(accommodationUnitFailureDeleteQueue, true);
    }

    @Bean
    public Queue priceFailureDeleteQueue() {
        return new Queue(priceFailureDeleteQueue, true);
    }

    @Bean
    public Queue addressFailureDeleteQueue() {
        return new Queue(addressFailureDeleteQueue, true);
    }

    @Bean
    public TopicExchange successDeleteExchange() {
        return new TopicExchange(successDeleteExchange);
    }

    @Bean
    public Queue accommodationSuccessDeleteQueue() {
        return new Queue(accommodationSuccessDeleteQueue, true);
    }

    @Bean
    public Queue accommodationUnitSuccessDeleteQueue() {
        return new Queue(accommodationUnitSuccessDeleteQueue, true);
    }

    @Bean
    public Queue priceSuccessDeleteQueue() {
        return new Queue(priceSuccessDeleteQueue, true);
    }

    @Bean
    public Queue addressSuccessDeleteQueue() {
        return new Queue(addressSuccessDeleteQueue, true);
    }


    @Bean
    public Binding accommodationFailureBinding() {
        return BindingBuilder
                .bind(accommodationFailureDeleteQueue())
                .to(failureDeleteExchange())
                .with(accommodationFailureDeleteRoutingKey);
    }

    @Bean
    public Binding accommodationUnitFailureBinding() {
        return BindingBuilder
                .bind(accommodationUnitFailureDeleteQueue())
                .to(failureDeleteExchange())
                .with(accommodationUnitFailureDeleteRoutingKey);
    }

    @Bean
    public Binding priceFailureDeleteBinding() {
        return BindingBuilder
                .bind(priceFailureDeleteQueue())
                .to(failureDeleteExchange())
                .with(priceFailureDeleteRoutingKey);
    }

    @Bean
    public Binding addressFailureDeleteBinding() {
        return BindingBuilder
                .bind(addressFailureDeleteQueue())
                .to(failureDeleteExchange())
                .with(addressFailureDeleteRoutingKey);
    }

    @Bean
    public Binding accommodationSuccessBinding() {
        return BindingBuilder
                .bind(accommodationSuccessDeleteQueue())
                .to(successDeleteExchange())
                .with(accommodationSuccessDeleteRoutingKey);
    }

    @Bean
    public Binding accommodationUnitSuccessBinding() {
        return BindingBuilder
                .bind(accommodationUnitSuccessDeleteQueue())
                .to(successDeleteExchange())
                .with(accommodationUnitSuccessDeleteRoutingKey);
    }

    @Bean
    public Binding priceSuccessDeleteBinding() {
        return BindingBuilder
                .bind(priceSuccessDeleteQueue())
                .to(successDeleteExchange())
                .with(priceSuccessDeleteRoutingKey);
    }

    @Bean
    public Binding addressSuccessDeleteBinding() {
        return BindingBuilder
                .bind(addressSuccessDeleteQueue())
                .to(successDeleteExchange())
                .with(addressSuccessDeleteRoutingKey);
    }



    @Bean
    public DirectExchange directExchange(
            @Value("${rabbitmq.crud-operations.direct-exchange.name}")
            String DirectExchangeRoute) {
        return new DirectExchange(DirectExchangeRoute);
    }


    @Bean
    public MessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonConverter());
        return factory;
    }
}
