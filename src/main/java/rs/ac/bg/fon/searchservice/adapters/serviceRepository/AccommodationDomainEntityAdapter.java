package rs.ac.bg.fon.searchservice.adapters.serviceRepository;

import rs.ac.bg.fon.searchservice.domain.AccommodationDomain;
import rs.ac.bg.fon.searchservice.dto.CriteriaQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccommodationDomainEntityAdapter {

    Page<AccommodationDomain> queryByRequiredCriteria(CriteriaQuery criteriaQuery, Pageable pageable);

}
