package rs.ac.bg.fon.searchservice.domain;

import rs.ac.bg.fon.searchservice.model.AccommodationType;
import rs.ac.bg.fon.searchservice.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccommodationDomain {

    private long id;
    private String name;
    private AccommodationType accommodationType;
    private Address address;

    private Set<AmenityDomain> amenities;
    private Set<AccommodationUnitDomain> accommodationUnits;

}
