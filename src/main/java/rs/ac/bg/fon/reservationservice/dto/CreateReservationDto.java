package rs.ac.bg.fon.reservationservice.dto;

import rs.ac.bg.fon.reservationservice.constraints.DateRangeConstraint;
import rs.ac.bg.fon.reservationservice.model.DateRange;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateReservationDto {

    @DateRangeConstraint
    private DateRange dateRange;
    @DecimalMin(value = "0.00")
    private BigDecimal totalAmount;
    @Min(value = 1)
    private int numberOfPeople;
    @NotNull
    private Long accommodationUnitId;

}


