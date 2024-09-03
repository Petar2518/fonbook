package rs.ac.bg.fon.accommodationservice.adapters.controllerServiceAdapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.dto.PriceDto;
import rs.ac.bg.fon.accommodationservice.dto.dateFilter.PricesDate;
import rs.ac.bg.fon.accommodationservice.dto.update.PriceDtoUpdate;
import rs.ac.bg.fon.accommodationservice.mapper.PriceMapper;
import rs.ac.bg.fon.accommodationservice.service.PriceService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PriceDtoDomainAdapter {
    private final PriceService priceService;
    private final PriceMapper priceMapper;


    public Long save(PriceDto priceDto) {
        return priceService.save(priceMapper.dtoToDomain(priceDto));
    }


    public List<PriceDto> getAll(Long accommodationId, PricesDate pricesDate) {
        return priceService.getAll(accommodationId, pricesDate)
                .stream()
                .map(priceMapper::domainToDto)
                .toList();
    }

    public PriceDto findById(Long id) {
        return priceMapper.domainToDto(
                priceService.findById(id));
    }

    public Long update(PriceDtoUpdate priceDto) {
        return priceService.update(priceMapper.dtoUpdateToDomainUpdate(priceDto));
    }

    public void deleteById(Long id) {
        priceService.deleteById(id);
    }
}
