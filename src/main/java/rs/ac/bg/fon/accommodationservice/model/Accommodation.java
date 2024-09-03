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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accommodations")
@EntityListeners(HibernateListener.class)
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accommodations_id_seq")
    @SequenceGenerator(name = "accommodations_id_seq", sequenceName = "accommodations_id_seq", allocationSize = 1)
    private Long id;

    private String name;

    private String description;

    @Column(name = "accommodation_type")
    @Enumerated(EnumType.STRING)
    private AccommodationType accommodationType;

    @Column(name = "host_id")
    private Long hostId;

    private boolean deleted;

    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "accommodation_amenities",
            joinColumns =
            @JoinColumn(name = "accommodation_id", referencedColumnName = "id"),
            inverseJoinColumns =
            @JoinColumn(name = "amenity_id", referencedColumnName = "id"))
    @Builder.Default
    private List<Amenity> amenities = new ArrayList<>();

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AccommodationUnit> units = new ArrayList<>();

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private final List<Image> images = new ArrayList<>();

}
