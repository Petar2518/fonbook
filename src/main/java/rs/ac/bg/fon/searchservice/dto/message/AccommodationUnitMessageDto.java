package rs.ac.bg.fon.searchservice.dto.message;

import rs.ac.bg.fon.searchservice.util.IdHolder;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccommodationUnitMessageDto implements Serializable, IdHolder {
    private Long id;

    private String name;

    private String description;

    private int capacity;

    private AccommodationMessageDto accommodation;


}

