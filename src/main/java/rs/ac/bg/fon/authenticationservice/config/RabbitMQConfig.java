package rs.ac.bg.fon.authenticationservice.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Getter
public class RabbitMQConfig {

    private final ConnectionFactory connectionFactory;

    @Value("${rabbitmq.exchanges.internal}")
    private String exchange;

    @Value("${rabbitmq.routing-keys.internal-email-registration}")
    private String registrationRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-email-verification}")
    private String verificationRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-email-forgot-password}")
    private String forgotPasswordRoutingKey;

    @Value("${rabbitmq.path.email-verification}")
    private String verificationEmailPath;

    @Bean
    public AmqpTemplate amqpTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
