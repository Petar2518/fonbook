package rs.ac.bg.fon.accommodationservice.domain;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDomain {
    private Long id;

    private byte[] image;

    private AccommodationDomain accommodation;
}
