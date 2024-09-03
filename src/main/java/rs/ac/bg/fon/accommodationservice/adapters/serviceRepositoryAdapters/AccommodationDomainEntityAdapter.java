package rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.mapper.AccommodationMapper;
import rs.ac.bg.fon.accommodationservice.repository.AccommodationRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccommodationDomainEntityAdapter {
    private final AccommodationMapper accommodationMapper;
    private final AccommodationRepository accRepository;


    public Long save(AccommodationDomain accommodationDomain) {
        return accRepository.save(accommodationMapper.domainToEntity(accommodationDomain)).getId();
    }


    public Optional<AccommodationDomain> findById(Long id) {
        return accRepository.findById(id)
                .map(accommodationMapper::entityToDomain);
    }


    public List<AccommodationDomain> findAll(Pageable pageable) {
        return accRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(accommodationMapper::entityToDomain)
                .toList();
    }

    public List<AccommodationDomain> findAllByHostId(Long hostId, Pageable pageable) {
        return accRepository.findAllByHostId(hostId, pageable).getContent()
                .stream().map(accommodationMapper::entityToDomain)
                .toList();
    }
}
