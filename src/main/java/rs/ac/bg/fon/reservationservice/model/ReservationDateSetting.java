package rs.ac.bg.fon.reservationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@Entity
@Builder
@NoArgsConstructor
@Table(name = "updated_requests")
public class ReservationDateSetting {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    @Column(name = "updated_check_in")
    private LocalDate updatedCheckIn;

    @Column(name = "updated_check_out")
    private LocalDate updatedCheckOut;

    private String message;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

}

