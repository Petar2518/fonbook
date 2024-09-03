package rs.ac.bg.fon.accommodationservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDto {
    private Long id;

    @NotNull
    private byte[] image;

    @NotNull
    private AccommodationDto accommodation;

}
