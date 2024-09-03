package rs.ac.bg.fon.searchservice.dto;

import rs.ac.bg.fon.searchservice.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDto {

    public long id;
    private LocalDate checkInDate;
    private LocalDate checkoutDate;
    private ReservationStatus status;

}
