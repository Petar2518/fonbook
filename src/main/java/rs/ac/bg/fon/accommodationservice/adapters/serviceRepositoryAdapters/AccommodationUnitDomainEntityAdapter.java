package rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.accommodationservice.mapper.AccommodationUnitMapper;
import rs.ac.bg.fon.accommodationservice.repository.AccommodationUnitRepository;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class AccommodationUnitDomainEntityAdapter {
    private final AccommodationUnitMapper accommodationUnitMapper;
    private final AccommodationUnitRepository accUnitRepository;


    public Long save(AccommodationUnitDomain accommodationUnitDomain) {

        return accUnitRepository.save(accommodationUnitMapper.domainToEntity(accommodationUnitDomain)).getId();

    }

    public Optional<AccommodationUnitDomain> findById(Long id) {
        return accUnitRepository.findById(id)
                .map(accommodationUnitMapper::entityToDomain);
    }


    public List<AccommodationUnitDomain> findAll(Long accommodationId, Pageable pageable) {
        return accUnitRepository.findAllByAccommodationId(accommodationId, pageable)
                .getContent()
                .stream()
                .map(accommodationUnitMapper::entityToDomain)
                .toList();
    }

    public List<AccommodationUnitDomain> findAllByHostId(Long hostId, Pageable pageable) {
        return accUnitRepository.findByAccommodationHostId(hostId, pageable).getContent()
                .stream().map(accommodationUnitMapper::entityToDomain)
                .toList();
    }


    public List<AccommodationUnitDomain> findAllByAccommodationId(Long accommodationId) {
        return accUnitRepository.findAllByAccommodationId(accommodationId).stream().map(accommodationUnitMapper::entityToDomain).toList();
    }
}
