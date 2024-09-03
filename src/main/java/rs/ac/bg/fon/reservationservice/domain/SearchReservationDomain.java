package rs.ac.bg.fon.reservationservice.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Data
@Builder
public class SearchReservationDomain {

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Pageable pageable;

    private Long profileId;
}
