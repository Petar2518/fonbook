package rs.ac.bg.fon.orchestratorservice.config;


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


    @Value("${rabbitmq.failure.delete.exchange}")
    private String failureDeleteExchange;
    
    @Value("${rabbitmq.failure.delete.accommodation.queue}")
    private String accommodationFailureDeleteQueue;

    @Value("${rabbitmq.failure.delete.accommodation.routing-key}")
    private String accommodationFailureDeleteRoutingKey;
    

    @Value("${rabbitmq.failure.delete.accommodation-unit.queue}")
    private String accommodationUnitFailureDeleteQueue;

    @Value("${rabbitmq.failure.delete.accommodation-unit.routing-key}")
    private String accommodationUnitFailureDeleteRoutingKey;
    

    @Value("${rabbitmq.failure.delete.address.queue}")
    private String addressFailureDeleteQueue;

    @Value("${rabbitmq.failure.delete.address.routing-key}")
    private String addressFailureDeleteRoutingKey;
    

    @Value("${rabbitmq.failure.delete.price.queue}")
    private String priceFailureDeleteQueue;

    @Value("${rabbitmq.failure.delete.price.routing-key}")
    private String priceFailureDeleteRoutingKey;

    

    @Value("${rabbitmq.success.delete.exchange}")
    private String successDeleteExchange;
    

    @Value("${rabbitmq.success.delete.accommodation.queue}")
    private String accommodationSuccessDeleteQueue;

    @Value("${rabbitmq.success.delete.accommodation.routing-key}")
    private String accommodationSuccessDeleteRoutingKey;


    @Value("${rabbitmq.success.delete.accommodation-unit.queue}")
    private String accommodationUnitSuccessDeleteQueue;

    @Value("${rabbitmq.success.delete.accommodation-unit.routing-key}")
    private String accommodationUnitSuccessDeleteRoutingKey;


    @Value("${rabbitmq.success.delete.address.queue}")
    private String addressSuccessDeleteQueue;

    @Value("${rabbitmq.success.delete.address.routing-key}")
    private String addressSuccessDeleteRoutingKey;


    @Value("${rabbitmq.success.delete.price.queue}")
    private String priceSuccessDeleteQueue;

    @Value("${rabbitmq.success.delete.price.routing-key}")
    private String priceSuccessDeleteRoutingKey;

    @Value("${rabbitmq.start.delete.exchange}")
    private String startDeleteExchange;


    @Value("${rabbitmq.start.delete.accommodation.queue}")
    private String accommodationStartDeleteQueue;

    @Value("${rabbitmq.start.delete.accommodation.routing-key}")
    private String accommodationStartDeleteRoutingKey;


    @Value("${rabbitmq.start.delete.accommodation-unit.queue}")
    private String accommodationUnitStartDeleteQueue;

    @Value("${rabbitmq.start.delete.accommodation-unit.routing-key}")
    private String accommodationUnitStartDeleteRoutingKey;


    @Value("${rabbitmq.start.delete.address.queue}")
    private String addressStartDeleteQueue;

    @Value("${rabbitmq.start.delete.address.routing-key}")
    private String addressStartDeleteRoutingKey;


    @Value("${rabbitmq.start.delete.price.queue}")
    private String priceStartDeleteQueue;

    @Value("${rabbitmq.start.delete.price.routing-key}")
    private String priceStartDeleteRoutingKey;
    
    

    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory);
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
    public TopicExchange startDeleteExchange() {
        return new TopicExchange(startDeleteExchange);
    }

    @Bean
    public Queue accommodationStartDeleteQueue() {
        return new Queue(accommodationStartDeleteQueue, true);
    }

    @Bean
    public Queue accommodationUnitStartDeleteQueue() {
        return new Queue(accommodationUnitStartDeleteQueue, true);
    }

    @Bean
    public Queue priceStartDeleteQueue() {
        return new Queue(priceStartDeleteQueue, true);
    }

    @Bean
    public Queue addressStartDeleteQueue() {
        return new Queue(addressStartDeleteQueue, true);
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
    public Binding priceFailureBinding() {
        return BindingBuilder
                .bind(priceFailureDeleteQueue())
                .to(failureDeleteExchange())
                .with(priceFailureDeleteRoutingKey);
    }

    @Bean
    public Binding addressFailureBinding() {
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
    public Binding priceSuccessBinding() {
        return BindingBuilder
                .bind(priceSuccessDeleteQueue())
                .to(successDeleteExchange())
                .with(priceSuccessDeleteRoutingKey);
    }

    @Bean
    public Binding addressSuccessBinding() {
        return BindingBuilder
                .bind(addressSuccessDeleteQueue())
                .to(successDeleteExchange())
                .with(addressSuccessDeleteRoutingKey);
    }




    @Bean
    public Binding accommodationStartBinding() {
        return BindingBuilder
                .bind(accommodationStartDeleteQueue())
                .to(startDeleteExchange())
                .with(accommodationStartDeleteRoutingKey);
    }

    @Bean
    public Binding accommodationUnitStartBinding() {
        return BindingBuilder
                .bind(accommodationUnitStartDeleteQueue())
                .to(startDeleteExchange())
                .with(accommodationUnitStartDeleteRoutingKey);
    }

    @Bean
    public Binding priceStartBinding() {
        return BindingBuilder
                .bind(priceStartDeleteQueue())
                .to(startDeleteExchange())
                .with(priceStartDeleteRoutingKey);
    }

    @Bean
    public Binding addressStartBinding() {
        return BindingBuilder
                .bind(addressStartDeleteQueue())
                .to(startDeleteExchange())
                .with(addressStartDeleteRoutingKey);
    }



    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonConverter());
        return factory;
    }
    @Bean
    public MessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
