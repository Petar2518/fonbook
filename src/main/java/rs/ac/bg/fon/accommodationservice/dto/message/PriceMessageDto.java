package rs.ac.bg.fon.accommodationservice.dto.message;


import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceMessageDto implements Serializable {
    private Long id;

    private BigDecimal amount;

    private String currency;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    private AccommodationUnitMessageDto accommodationUnit;

}
