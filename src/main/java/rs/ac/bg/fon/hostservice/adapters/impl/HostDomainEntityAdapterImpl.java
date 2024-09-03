package rs.ac.bg.fon.hostservice.adapters.impl;

import rs.ac.bg.fon.hostservice.adapters.HostDomainEntityAdapter;
import rs.ac.bg.fon.hostservice.domain.HostDomain;
import rs.ac.bg.fon.hostservice.mapper.HostMapper;
import rs.ac.bg.fon.hostservice.model.Host;
import rs.ac.bg.fon.hostservice.repository.HostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HostDomainEntityAdapterImpl implements HostDomainEntityAdapter {

    private final HostRepository hostRepository;
    private final HostMapper hostMapper;


    @Override
    public void save(HostDomain hostDomain) {
        hostRepository.save(hostMapper.domainToEntity(hostDomain));
    }

    @Override
    public Optional<HostDomain> getById(Long id) {
        return hostRepository
                .findById(id)
                .map(hostMapper::entityToDomain);
    }

    @Override
    public Page<HostDomain> getAll(Pageable pageable) {

        Page<Host> hostPage = hostRepository.findAll(pageable);

        List<HostDomain> hostDomainList = hostPage
                .getContent()
                .stream()
                .map(hostMapper::entityToDomain)
                .collect(Collectors.toList());

        return new PageImpl<>(hostDomainList, pageable, hostPage.getTotalElements());
    }

    @Override
    public void delete(Long  id) {
        hostRepository.deleteById(id);
    }

    @Override
    public void update(HostDomain hostDomain) {
         hostRepository.save(hostMapper.domainToEntity(hostDomain));
    }
}