package rs.ac.bg.fon.emailservice.service;

import rs.ac.bg.fon.emailservice.dto.request.ForgotPasswordRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.RegistrationEmailRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.ReservationEmailRequestDto;
import rs.ac.bg.fon.emailservice.dto.request.VerificationEmailRequestDto;

public interface EmailService {

    void sendRegistrationEmail(RegistrationEmailRequestDto registrationEmailRequest);

    void sendVerificationEmail(VerificationEmailRequestDto verificationEmailRequest);

    void sendForgotPasswordEmail(ForgotPasswordRequestDto forgotPasswordRequest);

    void sendReservationEmail(ReservationEmailRequestDto reservationEmailRequestDto);
}
