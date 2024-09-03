package rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.domain.ImageDomain;
import rs.ac.bg.fon.accommodationservice.mapper.ImageMapper;
import rs.ac.bg.fon.accommodationservice.repository.ImageRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ImageDomainEntityAdapter {
    private final ImageMapper imageMapper;
    private final ImageRepository imageRepository;

    public Long save(ImageDomain imageDomain) {
        return imageRepository.save(imageMapper.domainToEntity(imageDomain)).getId();
    }

    public Optional<ImageDomain> findById(Long id) {
        return imageRepository.findById(id)
                .map(imageMapper::entityToDomain);
    }

    public void deleteById(Long id) {
        imageRepository.deleteById(id);
    }

    public List<ImageDomain> findAll(Long accommodationId, Pageable pageable) {
        return imageRepository.findAllByAccommodationId(accommodationId, pageable)
                .getContent()
                .stream()
                .map(imageMapper::entityToDomain)
                .toList();
    }
}
