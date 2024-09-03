package rs.ac.bg.fon.hostservice.service;

import rs.ac.bg.fon.hostservice.domain.HostDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HostService {

    void save(HostDomain hostDomain);

    HostDomain getById(Long id);

    Page<HostDomain> getAll(Pageable pageable);

    void update(HostDomain hostDomain);

    void delete(Long id);

    void activateById(Long id);
}
