package rs.ac.bg.fon.emailservice.service.impl;

import rs.ac.bg.fon.emailservice.dto.request.ForgotPasswordRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.RegistrationEmailRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.ReservationEmailRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.VerificationEmailRequestDto;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {
    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(emailService, "registrationTemplateName", "registration_email.html");
        ReflectionTestUtils.setField(emailService, "verificationTemplateName", "verification_email.html");
        ReflectionTestUtils.setField(emailService, "forgotPasswordTemplateName", "forgot_password_email.html");
        ReflectionTestUtils.setField(emailService, "reservationTemplateName", "reservation_email.html");
    }

    @Test
    void sendRegistrationEmail() {
        RegistrationEmailRequestDto registrationEmailRequest = new RegistrationEmailRequestDto("example@example.com");
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("<html><body>Test</body></html>");


        emailService.sendRegistrationEmail(registrationEmailRequest);

        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendRegistrationEmailFailure() {
        ReflectionTestUtils.setField(emailService, "registrationTemplateName", "classpath:test.html");

        RegistrationEmailRequestDto registrationEmailRequest = new RegistrationEmailRequestDto("example@example.com");
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        assertThrows(RuntimeException.class, () -> emailService.sendRegistrationEmail(registrationEmailRequest));
    }

    @Test
    void sendVerificationEmail() {
        VerificationEmailRequestDto verificationEmailRequest = new VerificationEmailRequestDto("test@example.com", "http://localhost:8080/");

        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("<html><body>Test</body></html>");

        emailService.sendVerificationEmail(verificationEmailRequest);

        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendVerificationEmailFailure() {
        ReflectionTestUtils.setField(emailService, "verificationTemplateName", "test.html");

        VerificationEmailRequestDto verificationEmailRequest = new VerificationEmailRequestDto("test@example.com", "http://localhost:8080/");
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        assertThrows(RuntimeException.class, () -> emailService.sendVerificationEmail(verificationEmailRequest));
    }

    @Test
    void sendForgotPasswordEmail() {
        ForgotPasswordRequestDto forgotPasswordRequest = new ForgotPasswordRequestDto("test@example.com", UUID.randomUUID());

        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("<html><body>Test</body></html>");

        emailService.sendForgotPasswordEmail(forgotPasswordRequest);

        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendForgotPasswordEmailFailure() {
        ReflectionTestUtils.setField(emailService, "forgotPasswordTemplateName", "test.html");

        ForgotPasswordRequestDto forgotPasswordRequest = new ForgotPasswordRequestDto("test@example.com", UUID.randomUUID());
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        assertThrows(RuntimeException.class, () -> emailService.sendForgotPasswordEmail(forgotPasswordRequest));
    }

    @Test
    void sendReservationEmail() {
        ReservationEmailRequestDto reservationEmailRequest = new ReservationEmailRequestDto("test@example.com", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 5), new BigDecimal("500.00"), "USD", 2);

        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("<html><body>Test</body></html>");

        emailService.sendReservationEmail(reservationEmailRequest);

        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendReservationEmailFailure() {
        ReflectionTestUtils.setField(emailService, "reservationTemplateName", "test.html");

        ReservationEmailRequestDto reservationEmailRequest = new ReservationEmailRequestDto("test@example.com", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 5), new BigDecimal("500.00"), "USD", 2);
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        assertThrows(RuntimeException.class, () -> emailService.sendReservationEmail(reservationEmailRequest));
    }
}