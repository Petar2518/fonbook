package rs.ac.bg.fon.reservationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Entity
@Builder
@NoArgsConstructor
@Table(name = "reviews")
public class Review {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    Reservation reservation;

    private String title;

    private String comment;

    private Double rating;
    @Column(name= "accommodation_id")
    private Long accommodationId;


}
