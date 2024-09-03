package rs.ac.bg.fon.bookinggateway.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RegistrationDto(
        String email,
        String password,
        String role,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate dateOfBirth,
        String name,
        String bankAccountNumber) {
}
