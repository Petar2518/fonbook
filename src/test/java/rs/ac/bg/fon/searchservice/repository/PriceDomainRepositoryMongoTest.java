package rs.ac.bg.fon.searchservice.repository;

import rs.ac.bg.fon.searchservice.model.Price;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import util.MongoContainerInitializer;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
@Tag("datajpa")
public class PriceDomainRepositoryMongoTest extends MongoContainerInitializer {

    @Autowired
    PriceRepository priceRepository;

    @Test
    void saveAndFindById() {

        Price price = Price.builder()
                .id(17)
                .amount(BigDecimal.TEN)
                .dateFrom(LocalDate.now())
                .dateTo(LocalDate.now().plusDays(5))
                .build();

        priceRepository.save(price);


        Price retrievedPrice = priceRepository.findById(price.getId()).orElse(null);

        assertThat(retrievedPrice).isNotNull();
        assertThat(retrievedPrice).usingRecursiveComparison().isEqualTo(price);

    }
}
