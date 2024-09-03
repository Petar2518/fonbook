package rs.ac.bg.fon.searchservice.adapters.serviceRepository.impl;

import rs.ac.bg.fon.searchservice.adapters.serviceRepository.AccommodationDomainEntityAdapter;
import rs.ac.bg.fon.searchservice.domain.AccommodationDomain;
import rs.ac.bg.fon.searchservice.mapper.AccommodationMapper;
import rs.ac.bg.fon.searchservice.repository.AccommodationRepositoryQuery;
import rs.ac.bg.fon.searchservice.dto.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccommodationDomainEntityAdapterImpl implements AccommodationDomainEntityAdapter {

    private final AccommodationMapper accommodationMapper;
    private final AccommodationRepositoryQuery accommodationRepository;

    public Page<AccommodationDomain> queryByRequiredCriteria(CriteriaQuery criteriaQuery, Pageable pageable) {
        return accommodationMapper.entitiesToDomains(
                accommodationRepository.findAllByCriteria(criteriaQuery, pageable));

    }

}
