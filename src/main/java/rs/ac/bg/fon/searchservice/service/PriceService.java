package rs.ac.bg.fon.searchservice.service;

import rs.ac.bg.fon.searchservice.domain.PriceDomain;

public interface PriceService {

    void save(PriceDomain priceDomain);

    void deleteById(Long id);
}
