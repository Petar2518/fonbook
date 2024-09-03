package rs.ac.bg.fon.accommodationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.bg.fon.accommodationservice.eventListener.HibernateListener;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@Entity
@Table(name = "amenities")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(HibernateListener.class)

public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "amenities_id_seq")
    @SequenceGenerator(name = "amenities_id_seq", sequenceName = "amenities_id_seq", allocationSize = 1)
    private Long id;

    private String amenity;

    @ManyToMany(mappedBy = "amenities")
    @Builder.Default
    private final List<Accommodation> accommodations = new ArrayList<>();
}
