package rs.ac.bg.fon.accommodationservice.service;

import org.springframework.data.domain.Pageable;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AccommodationUnitDomainUpdate;

import java.util.List;

public interface AccommodationUnitService {
    Long save(AccommodationUnitDomain accommodationUnitDomain);

    List<AccommodationUnitDomain> getAll(Long accommodation, Pageable pageable);

    void deleteById(Long id);

    Long update(AccommodationUnitDomainUpdate accommodationUnitDomainUpdate);

    AccommodationUnitDomain findById(Long id);

    List<AccommodationUnitDomain> getAllByHost(Long hostId, Pageable pageable);

    void revertDelete(Long id);

    List<AccommodationUnitDomain> getAllByAccommodation(Long accommodationId);

    boolean deleteAllUnitsUnderAccommodation(Long id);

    boolean deleteByIdCascade(Long id);

    void revertDeleteCascade(Long id);
}
