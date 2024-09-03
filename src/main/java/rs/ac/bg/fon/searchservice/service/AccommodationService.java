package rs.ac.bg.fon.searchservice.service;


import rs.ac.bg.fon.searchservice.domain.AccommodationDomain;
import rs.ac.bg.fon.searchservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.searchservice.domain.AddressDomain;

public interface AccommodationService {

    void save(AccommodationDomain accommodationDomain);

    void addUnit(AccommodationUnitDomain unit, long id);

    void addAddress(AddressDomain addressDomain, long id);

    void deleteById(Long id);

    void deleteUnit(Long id);

    void deleteAddress(Long id);
}
