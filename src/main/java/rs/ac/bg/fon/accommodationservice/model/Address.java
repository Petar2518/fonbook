package rs.ac.bg.fon.accommodationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.bg.fon.accommodationservice.eventListener.HibernateListener;

@Builder
@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(HibernateListener.class)

public class Address {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Accommodation accommodation;

    private String country;

    private String city;

    private String street;

    @Column(name = "street_number")
    private String streetNumber;

    @Column(name = "postal_code")
    private String postalCode;


    @Column(name = "coordinate_latitude")
    private String latitude;


    @Column(name = "coordinate_longitude")
    private String longitude;

    private boolean deleted;

}
