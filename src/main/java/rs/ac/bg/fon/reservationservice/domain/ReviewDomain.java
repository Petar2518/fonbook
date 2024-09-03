package rs.ac.bg.fon.reservationservice.domain;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDomain {

    private Long id;

    private String title;

    private String comment;

    private Double rating;

    private Long accommodationId;

}

