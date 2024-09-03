package rs.ac.bg.fon.accommodationservice.service;

import org.springframework.data.domain.Pageable;
import rs.ac.bg.fon.accommodationservice.domain.ImageDomain;

import java.util.List;

public interface ImageService {
    Long save(ImageDomain imageDomain);

    List<ImageDomain> getAll(Long accommodation, Pageable pageable);

    void deleteById(Long id);

    ImageDomain findById(Long id);
}
