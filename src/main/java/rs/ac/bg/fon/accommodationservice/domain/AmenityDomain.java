package rs.ac.bg.fon.accommodationservice.domain;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmenityDomain {
    private Long id;

    private String amenity;
}
