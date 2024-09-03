package rs.ac.bg.fon.searchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccommodationDto {

    private long id;
    private String name;

    private AccommodationTypeDto accommodationType;
    private AddressDto address;
    private Set<AmenityDto> amenities;
    private Set<AccommodationUnitDto> accommodationUnits;
}
