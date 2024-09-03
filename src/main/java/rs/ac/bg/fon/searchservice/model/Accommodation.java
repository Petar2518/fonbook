package rs.ac.bg.fon.searchservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Document
@Data
@Builder
@AllArgsConstructor
public class Accommodation {

    @Id
    private long id;

    private String name;

    private AccommodationType accommodationType;

    @Field
    private Address address;

    @Field
    private Set<AccommodationUnit> accommodationUnits;

    private Set<Amenity> amenities;
}
