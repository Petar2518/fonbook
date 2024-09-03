package rs.ac.bg.fon.emailservice.rabbitmq;

import rs.ac.bg.fon.emailservice.dto.request.ForgotPasswordRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.RegistrationEmailRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.ReservationEmailRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.VerificationEmailRequestDto;
import rs.ac.bg.fon.emailservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "${rabbitmq.queues.email.registration}")
    public void registrationConsumer(RegistrationEmailRequestDto registrationRequest) {
        log.info("Consumed {} from queue", registrationRequest);
        emailService.sendRegistrationEmail(registrationRequest);
    }

    @RabbitListener(queues = "${rabbitmq.queues.email.verification}")
    public void verificationConsumer(VerificationEmailRequestDto verificationRequest) {
        log.info("Consumed {} from queue", verificationRequest);
        emailService.sendVerificationEmail(verificationRequest);
    }

    @RabbitListener(queues = "${rabbitmq.queues.email.forgot-password}")
    public void forgotPasswordConsumer(ForgotPasswordRequestDto forgotPasswordRequest) {
        log.info("Consumed {} from queue", forgotPasswordRequest);
        emailService.sendForgotPasswordEmail(forgotPasswordRequest);
    }

    @RabbitListener(queues = "${rabbitmq.queues.email.reservation}")
    public void reservationConsumer(ReservationEmailRequestDto reservationEmailRequestDto) {
        log.info("Consumed {} from queue", reservationEmailRequestDto);
        emailService.sendReservationEmail(reservationEmailRequestDto);
    }

}
