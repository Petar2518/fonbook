package rs.ac.bg.fon.reservationservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateReviewDto {

    @NotBlank
    private String title;

    private String comment;

    @Max(10)
    @Min(0)
    private Double rating;
}

