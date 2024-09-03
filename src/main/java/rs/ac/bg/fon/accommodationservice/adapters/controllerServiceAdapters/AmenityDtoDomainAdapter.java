package rs.ac.bg.fon.accommodationservice.adapters.controllerServiceAdapters;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.dto.AmenityDto;
import rs.ac.bg.fon.accommodationservice.mapper.AmenityMapper;
import rs.ac.bg.fon.accommodationservice.service.AmenityService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AmenityDtoDomainAdapter {
    private final AmenityService amenityService;
    private final AmenityMapper amenityMapper;


    public Long save(AmenityDto amenityDto) {
        return amenityService.save(amenityMapper.dtoToDomain(amenityDto));
    }


    public List<AmenityDto> getAll(Pageable pageable) {
        return amenityService.getAll(pageable)
                .stream()
                .map(amenityMapper::domainToDto)
                .toList();
    }

    public AmenityDto findById(Long id) {
        return amenityMapper.domainToDto(
                amenityService.findById(id));
    }

    public Long update(AmenityDto amenityDto) {
        return amenityService.update(amenityMapper.dtoToDomain(amenityDto));
    }

    public void deleteById(Long id) {
        amenityService.deleteById(id);
    }
}
