package rs.ac.bg.fon.searchservice.domain;

import rs.ac.bg.fon.searchservice.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document
@Data
@Builder
@AllArgsConstructor
public class ReservationDomain {

    private long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private ReservationStatus reservationStatus;
    private Long accommodationUnitId;
}
