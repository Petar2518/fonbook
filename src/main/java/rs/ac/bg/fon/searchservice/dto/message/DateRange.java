package rs.ac.bg.fon.searchservice.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DateRange {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
