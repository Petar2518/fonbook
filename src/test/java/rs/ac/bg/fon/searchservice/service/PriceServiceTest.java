package rs.ac.bg.fon.searchservice.service;

import rs.ac.bg.fon.searchservice.domain.PriceDomain;
import rs.ac.bg.fon.searchservice.mapper.PriceMapper;
import rs.ac.bg.fon.searchservice.model.Price;
import rs.ac.bg.fon.searchservice.repository.PriceRepository;
import rs.ac.bg.fon.searchservice.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PriceServiceTest {

    @Mock
    PriceRepository priceRepository;
    @Mock
    PriceMapper priceMapper;
    @InjectMocks
    PriceServiceImpl priceService;

    @Test
    public void saveAccommodation() {
        Price price = Price.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100))
                .dateFrom(LocalDate.now())
                .dateTo(LocalDate.now())
                .build();
        PriceDomain priceDomain = PriceDomain.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100))
                .dateFrom(LocalDate.now())
                .dateTo(LocalDate.now())
                .build();

        when(priceMapper.domainToEntity(priceDomain)).thenReturn(price);

        priceService.save(priceDomain);

        verify(priceRepository).save(price);
    }
}
