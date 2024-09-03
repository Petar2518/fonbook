package rs.ac.bg.fon.accommodationservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.model.Accommodation;

@Component
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    Page<Accommodation> findAllByHostId(Long hostId, Pageable pageable);
}
