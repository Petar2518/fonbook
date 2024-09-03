package rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.domain.AddressDomain;
import rs.ac.bg.fon.accommodationservice.mapper.AddressMapper;
import rs.ac.bg.fon.accommodationservice.repository.AddressRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AddressDomainEntityAdapter {
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;

    public Long save(AddressDomain addressDomain) {
        return addressRepository.save(addressMapper.domainToEntity(addressDomain)).getId();
    }

    public Optional<AddressDomain> findById(Long id) {
        return addressRepository.findById(id)
                .map(addressMapper::entityToDomain);
    }


    public List<AddressDomain> findAll(Pageable pageable) {
        return addressRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(addressMapper::entityToDomain)
                .toList();
    }
}
