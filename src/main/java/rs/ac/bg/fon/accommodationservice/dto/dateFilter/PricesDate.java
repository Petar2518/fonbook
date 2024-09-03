package rs.ac.bg.fon.accommodationservice.dto.dateFilter;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PricesDate {

    private LocalDate startDate;

    private LocalDate endDate;
}
