package rs.ac.bg.fon.emailservice.rabbitmq;


import rs.ac.bg.fon.emailservice.config.RabbitMQConfig;
import rs.ac.bg.fon.emailservice.dto.request.ForgotPasswordRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.RegistrationEmailRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.ReservationEmailRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.VerificationEmailRequestDto;
import rs.ac.bg.fon.emailservice.service.EmailService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("springboot")
@SpringBootTest
@Testcontainers
@ExtendWith(MockitoExtension.class)
public class EmailConsumerTest {

    @Container
    static RabbitMQContainer rabbitContainer = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine");

    @Autowired
    RabbitMQConfig rabbitMQConfig;
    @Autowired
    AmqpTemplate amqpTemplate;

    @Mock
    EmailService emailService;


    @Test
    void testRegistrationConsumer() {
        doNothing().when(emailService).sendRegistrationEmail(any());
        RegistrationEmailRequestDto registrationRequest = new RegistrationEmailRequestDto("test@example.com");

        amqpTemplate.convertAndSend(rabbitMQConfig.getEmailRegistrationQueue(), createMessage(registrationRequest));

        EmailConsumer emailConsumer = new EmailConsumer(emailService);

        emailConsumer.registrationConsumer(registrationRequest);

        verify(emailService, times(1)).sendRegistrationEmail(registrationRequest);
    }


    @Test
    void testVerificationConsumer() {
        doNothing().when(emailService).sendVerificationEmail(any());
        VerificationEmailRequestDto verificationRequest = new VerificationEmailRequestDto("test@example.com", "http://localhost:8080/accounts/verify-email");

        amqpTemplate.convertAndSend(rabbitMQConfig.getEmailVerificationQueue(), createMessage(verificationRequest));

        EmailConsumer emailConsumer = new EmailConsumer(emailService);

        emailConsumer.verificationConsumer(verificationRequest);

        verify(emailService, times(1)).sendVerificationEmail(verificationRequest);
    }

    @Test
    void testForgotPasswordConsumer() {
        doNothing().when(emailService).sendForgotPasswordEmail(any());
        ForgotPasswordRequestDto forgotPasswordRequest = new ForgotPasswordRequestDto("test@example.com", UUID.randomUUID());

        amqpTemplate.convertAndSend(rabbitMQConfig.getEmailVerificationQueue(), createMessage(forgotPasswordRequest));

        EmailConsumer emailConsumer = new EmailConsumer(emailService);

        emailConsumer.forgotPasswordConsumer(forgotPasswordRequest);

        verify(emailService, times(1)).sendForgotPasswordEmail(forgotPasswordRequest);
    }

    @Test
    void testReservationConsumer() {
        doNothing().when(emailService).sendReservationEmail(any());
        ReservationEmailRequestDto reservationEmailRequest = new ReservationEmailRequestDto("test@example.com", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 5), new BigDecimal("500.00"), "USD",2);

        amqpTemplate.convertAndSend(rabbitMQConfig.getEmailReservationQueue(), createMessage(reservationEmailRequest));

        EmailConsumer emailConsumer = new EmailConsumer(emailService);

        emailConsumer.reservationConsumer(reservationEmailRequest);

        verify(emailService, times(1)).sendReservationEmail(reservationEmailRequest);
    }

    private Message createMessage(Object payload) {
        return MessageBuilder.withBody(payload.toString().getBytes()).build();
    }
}
