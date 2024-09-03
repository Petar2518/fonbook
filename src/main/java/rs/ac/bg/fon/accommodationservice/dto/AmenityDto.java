package rs.ac.bg.fon.accommodationservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmenityDto {
    private Long id;

    @NotEmpty
    private String amenity;
}
