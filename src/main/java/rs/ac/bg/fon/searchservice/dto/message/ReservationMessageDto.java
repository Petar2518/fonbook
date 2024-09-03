package rs.ac.bg.fon.searchservice.dto.message;

import rs.ac.bg.fon.searchservice.model.ReservationStatus;
import rs.ac.bg.fon.searchservice.util.IdHolder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ReservationMessageDto implements IdHolder {

    private Long id;
    private LocalDate creationDate;
    private DateRange dateRange;
    private BigDecimal totalAmount;
    private ReservationStatus status;
    private int numberOfPeople;
    private Long profileId;
    private Long accommodationUnitId;
    private String currency;
    private boolean paid;

}