package rs.ac.bg.fon.accommodationservice.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceDomain {
    private Long id;
    private BigDecimal amount;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String currency;
    private AccommodationUnitDomain accommodationUnit;
    private boolean deleted;
}
