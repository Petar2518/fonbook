package rs.ac.bg.fon.searchservice.dto.message;

import rs.ac.bg.fon.searchservice.model.AccommodationType;
import rs.ac.bg.fon.searchservice.util.IdHolder;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccommodationMessageDto implements Serializable, IdHolder {

    private Long id;

    private String name;

    private String description;

    private AccommodationType accommodationType;

    private Long hostId;

    private List<AmenityMessageDto> amenities = new ArrayList<>();


}
