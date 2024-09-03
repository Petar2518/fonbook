package rs.ac.bg.fon.accommodationservice.service;

import org.springframework.data.domain.Pageable;
import rs.ac.bg.fon.accommodationservice.domain.AddressDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AddressDomainUpdate;

import java.util.List;

public interface AddressService {
    Long save(AddressDomain addressDomain);

    List<AddressDomain> getAll(Pageable pageable);

    void deleteById(Long id);

    Long update(AddressDomainUpdate addressDomainUpdate);

    AddressDomain findById(Long id);

    void revertDelete(Long id);

    boolean deleteByIdCascade(Long id);
}
