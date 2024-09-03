package rs.ac.bg.fon.reservationservice.domain;

import rs.ac.bg.fon.reservationservice.model.DateRange;
import rs.ac.bg.fon.reservationservice.model.RequestStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateReservationDateSettingDomain {

    private DateRange dateRange;

    private String message;

    private RequestStatus status;
}
