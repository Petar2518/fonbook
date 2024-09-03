package rs.ac.bg.fon.hostservice.adapters.impl;

import rs.ac.bg.fon.hostservice.adapters.HostDtoDomainAdapter;
import rs.ac.bg.fon.hostservice.dto.HostDto;
import rs.ac.bg.fon.hostservice.mapper.HostMapper;
import rs.ac.bg.fon.hostservice.service.HostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HostDtoDomainAdapterImpl implements HostDtoDomainAdapter {

    private final HostService hostService;
    private final HostMapper hostMapper;

    @Override
    public void save(HostDto hostDto) {
         hostService.save(hostMapper.dtoToDomain(hostDto));
    }

    @Override
    public HostDto getById(Long id) {
        return hostMapper.domainToDto(hostService.getById(id));
    }

    @Override
    public Page<HostDto> getAll(Pageable pageable) {
        return hostMapper.domainToDtoPage(hostService.getAll(pageable));
    }

    @Override
    public void delete(Long id) {
        hostService.delete(id);
    }

    @Override
    public void update(HostDto hostDto) {
         hostService.update(hostMapper.dtoToDomain(hostDto));
    }

    @Override
    public void activateById(Long id) {
        hostService.activateById(id);
    }
}
