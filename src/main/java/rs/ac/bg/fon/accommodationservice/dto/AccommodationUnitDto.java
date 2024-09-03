package rs.ac.bg.fon.accommodationservice.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccommodationUnitDto {
    private Long id;

    @NotEmpty
    private String name;

    private String description;

    @Min(1)
    @NotNull
    private int capacity;

    @NotNull
    private AccommodationDto accommodation;


}
