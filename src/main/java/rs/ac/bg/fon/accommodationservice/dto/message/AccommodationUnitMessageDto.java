package rs.ac.bg.fon.accommodationservice.dto.message;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccommodationUnitMessageDto implements Serializable {
    private Long id;

    private String name;

    private String description;

    private int capacity;

    private AccommodationMessageDto accommodation;


}

