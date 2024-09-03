package rs.ac.bg.fon.hostservice.service.impl;

import rs.ac.bg.fon.hostservice.adapters.HostDomainEntityAdapter;
import rs.ac.bg.fon.hostservice.domain.HostDomain;
import rs.ac.bg.fon.hostservice.exceptions.ActivateHostException;
import rs.ac.bg.fon.hostservice.exceptions.ResourceNotFoundException;
import rs.ac.bg.fon.hostservice.service.HostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HostServiceImpl implements HostService {

    private final HostDomainEntityAdapter domainEntityAdapter;

    @Override
    public void save(HostDomain hostDomain) {
        hostDomain.setActive(false);
        domainEntityAdapter
                .save(hostDomain);
    }

    @Override
    public HostDomain getById(Long id) {
        return domainEntityAdapter
                .getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Host", "id", id));
    }

    @Override
    public Page<HostDomain> getAll(Pageable pageable) {
        return domainEntityAdapter
                .getAll(pageable);
    }

    @Override
    public void delete(Long id) {
        domainEntityAdapter.delete(id);
    }

    @Override
    public void activateById(Long id) {
        HostDomain host = domainEntityAdapter
                .getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Host", "id", id));
        try {
            host.setActive(true);
            domainEntityAdapter.update(host);
        }catch (Exception e){
            throw new ActivateHostException(id);
        }

    }

    @Override
    public void update(HostDomain hostDomain) {
        HostDomain newHostDomain = this
                .getById(hostDomain.getId());

        Optional.ofNullable(hostDomain.getName())
                .ifPresent(newHostDomain::setName);

        Optional.ofNullable(hostDomain.getBankAccountNumber())
                .ifPresent(newHostDomain::setBankAccountNumber);

        Optional.ofNullable(hostDomain.getPhoneNumber())
                .ifPresent(newHostDomain::setPhoneNumber);

        domainEntityAdapter
                .update(newHostDomain);
    }
}
