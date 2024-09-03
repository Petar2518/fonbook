package rs.ac.bg.fon.accommodationservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.accommodationservice.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Page<Image> findAllByAccommodationId(Long accommodation, Pageable pageable);
}
