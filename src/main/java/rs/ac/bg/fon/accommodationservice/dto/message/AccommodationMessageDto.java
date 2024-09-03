package rs.ac.bg.fon.accommodationservice.dto.message;

import lombok.*;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccommodationMessageDto implements Serializable {

    private Long id;

    private String name;

    private String description;

    private AccommodationType accommodationType;

    private Long hostId;

    private List<AmenityMessageDto> amenities = new ArrayList<>();


}
