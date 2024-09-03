package rs.ac.bg.fon.reservationservice.feignclient;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Price {
    private Long id;


    private BigDecimal amount;


    private LocalDate dateFrom;


    private LocalDate dateTo;

    private String currency;

}

