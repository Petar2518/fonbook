package rs.ac.bg.fon.accommodationservice.domain;

import lombok.*;
import org.mapstruct.Mapping;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDomain {
    private Long id;
    private AccommodationDomain accommodation;
    private String country;
    private String city;
    private String street;
    private String streetNumber;
    private String postalCode;
    private String latitude;
    private String longitude;
    private boolean deleted;
}
