package rs.ac.bg.fon.reservationservice.dto;

import rs.ac.bg.fon.reservationservice.constraints.DateRangeConstraint;
import rs.ac.bg.fon.reservationservice.model.DateRange;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateReservationDateSettingDto {

    @DateRangeConstraint
    private DateRange dateRange;

    private String message;

}
