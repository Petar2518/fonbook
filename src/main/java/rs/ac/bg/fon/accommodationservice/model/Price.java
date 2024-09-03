package rs.ac.bg.fon.accommodationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.bg.fon.accommodationservice.constraint.DateConstraint;
import rs.ac.bg.fon.accommodationservice.eventListener.HibernateListener;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@Entity
@Table(name = "prices")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(HibernateListener.class)
@DateConstraint
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prices_id_seq")
    @SequenceGenerator(name = "prices_id_seq", sequenceName = "prices_id_seq", allocationSize = 1)
    private Long id;

    private BigDecimal amount;

    private String currency;

    private LocalDate dateFrom;

    private LocalDate dateTo;


    @ManyToOne(fetch = FetchType.LAZY)
    private AccommodationUnit accommodationUnit;

    private boolean deleted;

}
