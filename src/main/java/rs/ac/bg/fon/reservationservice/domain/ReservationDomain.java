package rs.ac.bg.fon.reservationservice.domain;

import rs.ac.bg.fon.reservationservice.model.DateRange;
import rs.ac.bg.fon.reservationservice.model.ReservationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ReservationDomain {

    private Long id;

    private LocalDate creationDate;

    private DateRange dateRange;

    private BigDecimal totalAmount;

    private ReservationStatus status;

    private int numberOfPeople;

    private Long accommodationUnitId;

    private Long profileId;

    private String currency;

    private boolean paid;

}


