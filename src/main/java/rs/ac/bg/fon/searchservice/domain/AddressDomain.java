package rs.ac.bg.fon.searchservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
@AllArgsConstructor
public class AddressDomain {


    private long id;
    private String country;
    private String city;
    private String street;
    private String streetNumber;
    private String postalCode;
}
