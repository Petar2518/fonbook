package rs.ac.bg.fon.hostservice.adapters;

import rs.ac.bg.fon.hostservice.dto.HostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HostDtoDomainAdapter {

    void save(HostDto hostDto);

    HostDto getById(Long id);

    Page<HostDto> getAll(Pageable pageable);

    void delete(Long id);

    void update(HostDto hostDto);


    void activateById(Long id);
}
