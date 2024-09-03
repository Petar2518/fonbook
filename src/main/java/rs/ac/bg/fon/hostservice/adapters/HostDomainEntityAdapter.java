package rs.ac.bg.fon.hostservice.adapters;

import rs.ac.bg.fon.hostservice.domain.HostDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface HostDomainEntityAdapter {

    void save(HostDomain hostDomain);

    Optional<HostDomain> getById(Long id);

    Page<HostDomain> getAll(Pageable pageable);

    void delete(Long id);

    void update(HostDomain hostDomain);

}
