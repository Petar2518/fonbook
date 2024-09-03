package rs.ac.bg.fon.accommodationservice.service;

import org.springframework.data.domain.Pageable;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AccommodationDomainUpdate;
import rs.ac.bg.fon.accommodationservice.model.JwtReceiver.UserInfo;

import java.util.List;

public interface AccommodationService {

    Long save(AccommodationDomain accommodationDomain, UserInfo userInfo);

    Long saveTry(AccommodationDomain accommodationDomain);

    List<AccommodationDomain> getAll(Pageable pageable);

    void deleteById(Long id, UserInfo userInfo);

    void deleteById(Long id);

    void revertAllCascade(Long id);

    Long update(AccommodationDomainUpdate accommodationDomainUpdate, UserInfo user);

    AccommodationDomain findById(Long id);

    List<AccommodationDomain> getAllByHost(Long hostId, Pageable pageable);

    boolean deleteAllUnitsAndAddressForAccommodation(Long id);

    void revertDelete(Long id);
}
