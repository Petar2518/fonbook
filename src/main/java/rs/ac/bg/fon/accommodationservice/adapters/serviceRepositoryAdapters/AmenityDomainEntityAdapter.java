package rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.domain.AmenityDomain;
import rs.ac.bg.fon.accommodationservice.mapper.AmenityMapper;
import rs.ac.bg.fon.accommodationservice.repository.AmenityRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AmenityDomainEntityAdapter {
    private final AmenityMapper amenityMapper;
    private final AmenityRepository amenityRepository;

    public Long save(AmenityDomain amenityDomain) {
        return amenityRepository.save(amenityMapper.domainToEntity(amenityDomain)).getId();
    }

    public Optional<AmenityDomain> findById(Long id) {
        return amenityRepository.findById(id)
                .map(amenityMapper::entityToDomain);
    }

    public void deleteById(Long id) {
        amenityRepository.deleteById(id);
    }

    public List<AmenityDomain> findAll(Pageable pageable) {
        return amenityRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(amenityMapper::entityToDomain)
                .toList();
    }
}
