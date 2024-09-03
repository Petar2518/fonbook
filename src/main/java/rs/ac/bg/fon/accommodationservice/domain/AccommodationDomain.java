package rs.ac.bg.fon.accommodationservice.domain;

import lombok.*;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccommodationDomain {
    private Long id;
    private String name;
    private String description;
    private AccommodationType accommodationType;
    private Long hostId;
    private boolean deleted;

    @Builder.Default
    private List<AmenityDomain> amenities = new ArrayList<>();

    @Builder.Default
    private List<AccommodationUnitDomain> units = new ArrayList<>();
}
