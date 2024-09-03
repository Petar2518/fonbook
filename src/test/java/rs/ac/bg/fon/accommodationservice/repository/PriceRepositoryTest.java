package rs.ac.bg.fon.accommodationservice.repository;

import rs.ac.bg.fon.accommodationservice.model.Accommodation;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.AccommodationUnit;
import rs.ac.bg.fon.accommodationservice.model.Price;
import rs.ac.bg.fon.accommodationservice.util.DataJpaTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("datajpa")
class PriceRepositoryTest extends DataJpaTestBase {
    @Autowired
    PriceRepository priceRepository;

    @Autowired
    AccommodationRepository accRepository;

    @Autowired
    AccommodationUnitRepository accUnitRepository;


    @Test
    void saveAndFindById() {
        Accommodation acc = Accommodation.builder()
                .name("Apartment 1")
                .accommodationType(AccommodationType.APARTMENT)
                .hostId(5L)
                .build();

        Accommodation accSaved = accRepository.save(acc);

        AccommodationUnit accUnit = AccommodationUnit.builder()
                .accommodation(accSaved)
                .capacity(5)
                .name("Family room")
                .description("Nice quiet room")
                .build();


        AccommodationUnit accUnitSaved = accUnitRepository.save(accUnit);
        LocalDate from = LocalDate.of(2024, Calendar.NOVEMBER, 20);
        LocalDate to = LocalDate.of(2024, Calendar.DECEMBER, 10);
        Price price = Price.builder()
                .currency("EUR")
                .amount(BigDecimal.valueOf(100.00))
                .accommodationUnit(accUnitSaved)
                .dateFrom(from)
                .dateTo(to)
                .build();
        Price priceSaved = priceRepository.save(price);
        Optional<Price> actual = priceRepository.findById(priceSaved.getId());

        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(priceSaved);

    }
}