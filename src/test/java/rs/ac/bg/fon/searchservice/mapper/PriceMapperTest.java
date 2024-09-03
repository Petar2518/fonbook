package rs.ac.bg.fon.searchservice.mapper;

import rs.ac.bg.fon.searchservice.domain.PriceDomain;
import rs.ac.bg.fon.searchservice.dto.message.PriceMessageDto;
import rs.ac.bg.fon.searchservice.model.AccommodationUnit;
import rs.ac.bg.fon.searchservice.model.Price;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("springboot")
class PriceMapperTest {

    @Autowired
    PriceMapper priceMapper;

    static Price price;

    @BeforeAll
    static void setUp() {

        AccommodationUnit accommodationUnit = AccommodationUnit.builder()
                .id(555L)
                .capacity(5)
                .build();

        price = Price.builder()
                .id(998)
                .amount(BigDecimal.TEN)
                .dateFrom(LocalDate.now().plusDays(15))
                .dateTo(LocalDate.now().plusDays(20))
                .build();
    }

    @Test
    void domainToEntity() {
        PriceDomain mappedPrice = priceMapper.entityToDomain(price);

        Assertions.assertEquals(mappedPrice.getId(), price.getId());
        Assertions.assertEquals(mappedPrice.getId(), price.getId());
    }

    @Test
    void messageDtoToDomain() {
        PriceMessageDto priceMessageDto = PriceMessageDto.builder()
                .id(1L)
                .amount(BigDecimal.ONE)
                .currency("e")
                .dateFrom(LocalDate.now())
                .dateTo(LocalDate.now().plusDays(10))
                .build();
        PriceDomain expected = PriceDomain.builder()
                .id(1L)
                .amount(BigDecimal.ONE)
                .dateFrom(LocalDate.now())
                .dateTo(LocalDate.now().plusDays(10))
                .build();

        PriceDomain priceDomain = priceMapper.messageDtoToDomain(priceMessageDto);

        assertThat(priceDomain).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void entityToDomain() {
    }

    @Test
    void entitiesToDomains() {
    }
}