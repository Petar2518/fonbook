package rs.ac.bg.fon.accommodationservice.adapters.controllerServiceAdapters;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationUnitDto;
import rs.ac.bg.fon.accommodationservice.dto.update.AccommodationUnitDtoUpdate;
import rs.ac.bg.fon.accommodationservice.mapper.AccommodationUnitMapper;
import rs.ac.bg.fon.accommodationservice.service.AccommodationUnitService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccommodationUnitDtoDomainAdapter {

    private final AccommodationUnitService accUnitService;
    private final AccommodationUnitMapper accUnitMapper;


    public Long save(AccommodationUnitDto accommodationUnitDto) {
        return accUnitService.save(accUnitMapper.dtoToDomain(accommodationUnitDto));
    }

    public List<AccommodationUnitDto> getAll(Long accommodationId, Pageable pageable) {
        return accUnitService.getAll(accommodationId, pageable)
                .stream()
                .map(accUnitMapper::domainToDto)
                .toList();
    }

    public AccommodationUnitDto findById(Long id) {
        return accUnitMapper.domainToDto(
                accUnitService.findById(id));
    }

    public Long update(AccommodationUnitDtoUpdate accommodationUnitDtoUpdate) {
        return accUnitService.update(accUnitMapper.dtoUpdateToDomainUpdate(accommodationUnitDtoUpdate));
    }

    public void deleteById(Long id) {
        accUnitService.deleteById(id);
    }

    public List<AccommodationUnitDto> getAllByHost(Long hostId, Pageable pageable) {
        return accUnitService.getAllByHost(hostId, pageable)
                .stream()
                .map(accUnitMapper::domainToDto)
                .toList();
    }
}
