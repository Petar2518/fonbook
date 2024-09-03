package rs.ac.bg.fon.accommodationservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.accommodationservice.model.AccommodationUnit;

import java.util.List;

@Component
public interface AccommodationUnitRepository extends JpaRepository<AccommodationUnit, Long> {

    Page<AccommodationUnit> findAllByAccommodationId(Long accommodationId, Pageable pageable);

    Page<AccommodationUnit> findByAccommodationHostId(Long hostId, Pageable pageable);


    List<AccommodationUnit> findAllByAccommodationId(Long accommodationId);
}
