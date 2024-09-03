package rs.ac.bg.fon.searchservice.dto.message;

import rs.ac.bg.fon.searchservice.util.IdHolder;
import lombok.*;

import java.io.Serializable;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressMessageDto implements Serializable, IdHolder {


    private Long id;

    private AccommodationMessageDto accommodation;

    private String country;

    private String city;

    private String street;

    private String streetNumber;

    private String postalCode;


    private String latitude;


    private String longitude;


}
