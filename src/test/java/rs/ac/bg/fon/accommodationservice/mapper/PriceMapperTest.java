package rs.ac.bg.fon.accommodationservice.mapper;

import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.accommodationservice.domain.PriceDomain;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationDto;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationUnitDto;
import rs.ac.bg.fon.accommodationservice.dto.PriceDto;
import rs.ac.bg.fon.accommodationservice.model.Accommodation;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.AccommodationUnit;
import rs.ac.bg.fon.accommodationservice.model.Price;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PriceMapperTest {
    PriceMapper mapper = Mappers.getMapper(PriceMapper.class);

    AccommodationUnitMapper accommodationUnitMapper = Mappers.getMapper(AccommodationUnitMapper.class);

    @Test
    void fromEntityToDomain() {
        Accommodation accommodation = Accommodation.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AccommodationUnit accommodationUnit = AccommodationUnit.builder()
                .name("King size bedroom")
                .capacity(2)
                .description("Large room")
                .accommodation(accommodation)
                .build();

        Price price = Price.builder()
                .id(1L)
                .dateFrom(LocalDate.of(2025, 3, 24))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnit)
                .build();
        PriceDomain priceDomain = mapper.entityToDomain(price);

        assertNotNull(priceDomain);
        assertEquals(priceDomain.getDateFrom(), price.getDateFrom());
        assertEquals(priceDomain.getDateTo(), price.getDateTo());
        assertEquals(priceDomain.getAmount(), price.getAmount());
        assertEquals(priceDomain.getCurrency(), price.getCurrency());
        assertEquals(priceDomain.getAccommodationUnit().getId(), accommodationUnitMapper.entityToDomain(price.getAccommodationUnit()).getId());
    }

    @Test
    void fromDomainToEntity() {

        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AccommodationUnitDomain accommodationUnitDomain = AccommodationUnitDomain.builder()
                .name("King size bedroom")
                .capacity(2)
                .description("Large room")
                .accommodation(accommodationDomain)
                .build();

        PriceDomain priceDomain = PriceDomain.builder()
                .id(1L)
                .dateFrom(LocalDate.of(2025, 3, 24))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDomain)
                .build();
        Price price = mapper.domainToEntity(priceDomain);

        assertNotNull(price);
        assertEquals(priceDomain.getDateFrom(), price.getDateFrom());
        assertEquals(priceDomain.getDateTo(), price.getDateTo());
        assertEquals(priceDomain.getAmount(), price.getAmount());
        assertEquals(priceDomain.getCurrency(), price.getCurrency());
        assertEquals(priceDomain.getAccommodationUnit().getId(), accommodationUnitMapper.entityToDomain(price.getAccommodationUnit()).getId());
    }

    @Test
    void fromDomainToDto() {
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AccommodationUnitDomain accommodationUnitDomain = AccommodationUnitDomain.builder()
                .name("King size bedroom")
                .capacity(2)
                .description("Large room")
                .accommodation(accommodationDomain)
                .build();

        PriceDomain priceDomain = PriceDomain.builder()
                .id(1L)
                .dateFrom(LocalDate.of(2025, 3, 24))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDomain)
                .build();
        PriceDto priceDto = mapper.domainToDto(priceDomain);

        assertNotNull(priceDto);
        assertEquals(priceDomain.getDateFrom(), priceDto.getDateFrom());
        assertEquals(priceDomain.getDateTo(), priceDto.getDateTo());
        assertEquals(priceDomain.getAmount(), priceDto.getAmount());
        assertEquals(priceDomain.getCurrency(), priceDto.getCurrency());
        assertEquals(priceDomain.getAccommodationUnit().getId(), accommodationUnitMapper.dtoToDomain(priceDto.getAccommodationUnit()).getId());
    }

    @Test
    void fromDtoToDomain() {
        AccommodationDto accommodationDto = AccommodationDto.builder()
                .name("Hilton Belgrade")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(5L)
                .description("Nice hotel")
                .build();
        AccommodationUnitDto accommodationUnitDto = AccommodationUnitDto.builder()
                .name("King size bedroom")
                .capacity(2)
                .description("Large room")
                .accommodation(accommodationDto)
                .build();

        PriceDto priceDto = PriceDto.builder()
                .id(1L)
                .dateFrom(LocalDate.of(2025, 3, 24))
                .dateTo(LocalDate.of(2025, 3, 26))
                .amount(BigDecimal.valueOf(110.00))
                .currency("EUR")
                .accommodationUnit(accommodationUnitDto)
                .build();
        PriceDomain priceDomain = mapper.dtoToDomain(priceDto);

        assertNotNull(priceDto);
        assertEquals(priceDomain.getDateFrom(), priceDto.getDateFrom());
        assertEquals(priceDomain.getDateTo(), priceDto.getDateTo());
        assertEquals(priceDomain.getAmount(), priceDto.getAmount());
        assertEquals(priceDomain.getCurrency(), priceDto.getCurrency());
        assertEquals(priceDomain.getAccommodationUnit().getId(), accommodationUnitMapper.dtoToDomain(priceDto.getAccommodationUnit()).getId());

    }
}
