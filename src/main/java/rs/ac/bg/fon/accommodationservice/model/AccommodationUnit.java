package rs.ac.bg.fon.accommodationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.AfterMapping;
import rs.ac.bg.fon.accommodationservice.eventListener.HibernateListener;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accommodation_units")
@EntityListeners(HibernateListener.class)

public class AccommodationUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accommodation_units_id_seq")
    @SequenceGenerator(name = "accommodation_units_id_seq", sequenceName = "accommodation_units_id_seq", allocationSize = 1)
    private Long id;

    private String name;

    private String description;

    private int capacity;

    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    private Accommodation accommodation;

    @OneToMany(mappedBy = "accommodationUnit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Price> prices = new ArrayList<>();
}
