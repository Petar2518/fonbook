package rs.ac.bg.fon.accommodationservice.service;

import rs.ac.bg.fon.accommodationservice.domain.PriceDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.PriceDomainUpdate;
import rs.ac.bg.fon.accommodationservice.dto.dateFilter.PricesDate;

import java.util.List;

public interface PriceService {
    Long save(PriceDomain priceDomain);

    List<PriceDomain> getAll(Long accommodation, PricesDate pricesDate);

    void deleteById(Long id);

    Long update(PriceDomainUpdate priceDomain);

    PriceDomain findById(Long id);

    void revertDelete(Long id);

    boolean deletePricesUnderUnit(Long id);

    boolean deleteByIdCascade(Long id);

    void revertDeleteCascade(Long id);
}
