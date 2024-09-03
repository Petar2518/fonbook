package rs.ac.bg.fon.searchservice.repository;

import rs.ac.bg.fon.searchservice.dto.CriteriaQuery;
import rs.ac.bg.fon.searchservice.model.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationRepositoryQuery {
    Page<Accommodation> findAllByCriteria(CriteriaQuery criteriaQuery, Pageable pageable);

}
