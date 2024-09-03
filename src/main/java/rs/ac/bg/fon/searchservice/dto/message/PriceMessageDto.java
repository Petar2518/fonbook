package rs.ac.bg.fon.searchservice.dto.message;


import rs.ac.bg.fon.searchservice.util.IdHolder;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceMessageDto implements Serializable, IdHolder {
    private Long id;

    private BigDecimal amount;

    private String currency;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    private AccommodationUnitMessageDto accommodationUnit;

}
