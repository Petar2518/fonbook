package rs.ac.bg.fon.searchservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document
@Data
@Builder
@AllArgsConstructor
public class PriceDomain {


    private long id;
    private BigDecimal amount;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    private Long accommodationUnitId;

    private double meanPrice;

    private double priceForRange;

    private BigDecimal totalAmount;

}
