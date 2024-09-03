package rs.ac.bg.fon.accommodationservice.service;

import org.springframework.data.domain.Pageable;
import rs.ac.bg.fon.accommodationservice.domain.AmenityDomain;

import java.util.List;

public interface AmenityService {

    Long save(AmenityDomain amenityDomain);

    List<AmenityDomain> getAll(Pageable pageable);

    void deleteById(Long id);

    Long update(AmenityDomain amenityDomain);

    AmenityDomain findById(Long id);
}
