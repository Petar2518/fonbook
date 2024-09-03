package rs.ac.bg.fon.searchservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document
@Data
@Builder
@AllArgsConstructor
public class AccommodationUnit {

    @Id
    private Long id;

    private Integer capacity;

    Set<Reservation> reservations;
    Set<Price> prices;

}
