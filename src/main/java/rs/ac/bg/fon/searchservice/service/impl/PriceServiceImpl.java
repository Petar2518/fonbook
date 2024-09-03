package rs.ac.bg.fon.searchservice.service.impl;

import rs.ac.bg.fon.searchservice.domain.PriceDomain;
import rs.ac.bg.fon.searchservice.mapper.PriceMapper;
import rs.ac.bg.fon.searchservice.model.Price;
import rs.ac.bg.fon.searchservice.repository.PriceRepository;
import rs.ac.bg.fon.searchservice.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceServiceImpl implements PriceService {

    private final PriceMapper mapper;
    private final PriceRepository priceRepository;

    @Override
    public void save(PriceDomain priceDomain) {
        Price price = mapper.domainToEntity(priceDomain);

        priceRepository.save(price);
        log.info("Saved price {}", price);
    }

    @Override
    public void deleteById(Long id) {
        priceRepository.deleteById(id);
        log.info("Deleted price with id {}", id);
    }
}
