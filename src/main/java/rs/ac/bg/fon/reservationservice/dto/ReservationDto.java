package rs.ac.bg.fon.reservationservice.dto;

import rs.ac.bg.fon.reservationservice.model.DateRange;
import rs.ac.bg.fon.reservationservice.model.ReservationStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ReservationDto {

    @NotNull
    private Long id;

    @NotNull
    private LocalDate creationDate;

    private DateRange dateRange;

    @DecimalMin(value = "0.00")
    private BigDecimal totalAmount;

    @NotNull
    private ReservationStatus status;

    @Min(value = 1)
    private int numberOfPeople;

    @NotNull
    private Long profileId;

    @NotNull
    private Long accommodationUnitId;

    @NotNull
    private String currency;

    @NotNull
    private boolean paid;
}


