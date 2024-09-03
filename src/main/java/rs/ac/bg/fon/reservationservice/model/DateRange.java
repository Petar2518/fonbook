package rs.ac.bg.fon.reservationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateRange {

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

}
