package rs.ac.bg.fon.emailservice.service.impl;

import rs.ac.bg.fon.emailservice.dto.request.ForgotPasswordRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.RegistrationEmailRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.ReservationEmailRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.VerificationEmailRequestDto;
import rs.ac.bg.fon.emailservice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${templates.name.registration}")
    private String registrationTemplateName;

    @Value("${templates.name.verification}")
    private String verificationTemplateName;

    @Value("${templates.name.forgot-password}")
    private String forgotPasswordTemplateName;

    @Value("${templates.name.reservation}")
    private String reservationTemplateName;

    @Override
    public void sendRegistrationEmail(RegistrationEmailRequestDto registrationEmailRequest) {
        sendEmail(registrationTemplateName, "Successful registration", registrationEmailRequest.to(), new Context());
    }

    @Override
    public void sendVerificationEmail(VerificationEmailRequestDto verificationEmailRequest) {
        Context context = new Context();
        context.setVariable("link", verificationEmailRequest.link());


        sendEmail(verificationTemplateName, "Email verification", verificationEmailRequest.to(), context);
    }


    @Override
    public void sendForgotPasswordEmail(ForgotPasswordRequestDto forgotPasswordRequest) {
        Context context = new Context();
        context.setVariable("resetCode", forgotPasswordRequest.id());

        sendEmail(forgotPasswordTemplateName, "Forgot password", forgotPasswordRequest.email(), context);
    }

    @Override
    public void sendReservationEmail(ReservationEmailRequestDto reservationEmailRequestDto) {
        Context context = new Context();
        context.setVariable("checkInDate", reservationEmailRequestDto.checkInDate());
        context.setVariable("checkOutDate", reservationEmailRequestDto.checkOutDate());
        context.setVariable("totalAmount", reservationEmailRequestDto.totalAmount());
        context.setVariable("numberOfPeople", reservationEmailRequestDto.numberOfPeople());
        context.setVariable("currency", reservationEmailRequestDto.currency());

        sendEmail(reservationTemplateName, "Successful Reservation", reservationEmailRequestDto.email(), context);

    }

    private void sendEmail(String templateName, String subject, String to, Context context) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = templateEngine.process(templateName, context);

            helper.setText(htmlContent, true);
            helper.setTo(to);
            helper.setSubject(subject);

            javaMailSender.send(mimeMessage);
            log.info("Email is sent to {}", to);
        } catch (MessagingException ex) {
            log.error("Failed to send email", ex);
            throw new IllegalArgumentException("Failed to send email");
        }
    }
}
