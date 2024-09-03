package rs.ac.bg.fon.accommodationservice.domain.update;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDomainUpdate {
    private Long id;
    private String street;
    private String streetNumber;
    private String postalCode;
    private String latitude;
    private String longitude;
}
