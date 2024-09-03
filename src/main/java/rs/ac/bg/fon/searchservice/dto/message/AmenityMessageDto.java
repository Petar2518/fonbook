package rs.ac.bg.fon.searchservice.dto.message;

import rs.ac.bg.fon.searchservice.util.IdHolder;
import lombok.*;

import java.io.Serializable;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmenityMessageDto implements Serializable, IdHolder {
    private Long id;

    private String amenity;

}
