package rs.ac.bg.fon.searchservice.dto;

import rs.ac.bg.fon.searchservice.constraint.AccommodationTypeConstraint;
import rs.ac.bg.fon.searchservice.constraint.DateConstraint;
import rs.ac.bg.fon.searchservice.constraint.PriceConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@PriceConstraint
@DateConstraint
public class SearchRequest {


    @Builder.Default
    private String name = "";

    @Builder.Default
    @AccommodationTypeConstraint
    private String type = "";

    @Builder.Default
    private String city = "";

    @Builder.Default
    private String country = "";

    @Min(message = "Capacity must be higher than one", value = 1)
    private Integer capacity;

    private LocalDate checkIn;
    private LocalDate checkOut;

    @Positive
    private Double minPrice;
    @Positive
    private Double maxPrice;

    private List<String> amenities;
}
