package rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.domain.PriceDomain;
import rs.ac.bg.fon.accommodationservice.mapper.AccommodationUnitMapper;
import rs.ac.bg.fon.accommodationservice.mapper.PriceMapper;
import rs.ac.bg.fon.accommodationservice.repository.PriceRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PriceDomainEntityAdapter {
    private final PriceMapper priceMapper;
    private final PriceRepository priceRepository;
    private final AccommodationUnitMapper accommodationUnitMapper;

    public Long save(PriceDomain priceDomain) {
        return priceRepository.save(priceMapper.domainToEntity(priceDomain)).getId();
    }

    public Optional<PriceDomain> findById(Long id) {
        return priceRepository.findById(id)
                .map(priceMapper::entityToDomain);
    }


    public List<PriceDomain> findPricesForDatesForAccommodationUnit(Long accommodationUnit, LocalDate before, LocalDate after) {
        return priceRepository.findAllAccommodationUnitsInDates(accommodationUnit, before, after)
                .stream().map(priceMapper::entityToDomain)
                .toList();
    }

    public List<PriceDomain> findByUnitId(Long id) {
        return priceRepository.findAllPricesForAccommodationUnit(id).stream().map(priceMapper::entityToDomain)
                .toList();
    }
}
