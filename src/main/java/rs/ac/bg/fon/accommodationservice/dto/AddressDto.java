package rs.ac.bg.fon.accommodationservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDto {
    private Long id;
    private AccommodationDto accommodation;
    @NotEmpty
    private String country;
    @NotEmpty
    private String city;
    @NotEmpty
    private String street;
    @NotEmpty
    private String streetNumber;
    @NotEmpty
    private String postalCode;
    private String latitude;
    private String longitude;
}
