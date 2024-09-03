package rs.ac.bg.fon.accommodationservice.dto.update;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDtoUpdate {
    private Long id;

    @NotEmpty
    private String street;
    @NotEmpty
    private String streetNumber;
    @NotEmpty
    private String postalCode;
    private String latitude;
    private String longitude;
}
