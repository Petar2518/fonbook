package rs.ac.bg.fon.reservationservice.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Data
@Builder
public class SearchReservationDto {

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Pageable pageable;
}
