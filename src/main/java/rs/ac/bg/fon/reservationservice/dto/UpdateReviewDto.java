package rs.ac.bg.fon.reservationservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateReviewDto {

    private String title;

    private String  comment;

    @Max(10)
    @Min(0)
    private Double rating;

}
