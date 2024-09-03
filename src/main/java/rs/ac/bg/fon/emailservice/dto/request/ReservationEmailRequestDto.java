package rs.ac.bg.fon.emailservice.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationEmailRequestDto(
        String email,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BigDecimal totalAmount,
        String currency,
        Integer numberOfPeople
) {
}
