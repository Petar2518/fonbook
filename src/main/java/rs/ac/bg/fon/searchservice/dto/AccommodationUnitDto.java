package rs.ac.bg.fon.searchservice.dto;


import rs.ac.bg.fon.searchservice.model.Price;
import rs.ac.bg.fon.searchservice.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document
@Data
@Builder
@AllArgsConstructor
public class AccommodationUnitDto {

    private long id;

    private int capacity;

    Set<Reservation> reservations;
    Set<Price> prices;

}
