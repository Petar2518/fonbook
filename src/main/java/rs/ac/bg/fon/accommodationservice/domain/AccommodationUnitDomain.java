package rs.ac.bg.fon.accommodationservice.domain;

import lombok.*;
import rs.ac.bg.fon.accommodationservice.model.Price;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccommodationUnitDomain {
    private Long id;
    private String name;
    private String description;
    private int capacity;
    private AccommodationDomain accommodation;
    private boolean deleted;

    @Builder.Default
    private List<Price> prices = new ArrayList<>();

}
