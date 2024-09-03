package rs.ac.bg.fon.reservationservice.model;

import rs.ac.bg.fon.reservationservice.eventListener.HibernateListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Data
@Entity
@Builder
@NoArgsConstructor
@Table(name = "reservations")
@EntityListeners(HibernateListener.class)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_id_seq")
    @SequenceGenerator(name = "reservation_id_seq", sequenceName = "reservation_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "number_of_people")
    private int numberOfPeople;

    @Column(name = "accommodation_unit_id")
    private Long accommodationUnitId;

    @Column(name = "profile_id")
    private Long profileId;

    private String currency;

    private boolean paid;

}
