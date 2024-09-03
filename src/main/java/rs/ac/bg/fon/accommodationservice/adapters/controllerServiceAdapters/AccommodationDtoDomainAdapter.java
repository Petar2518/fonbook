package rs.ac.bg.fon.accommodationservice.adapters.controllerServiceAdapters;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationDto;
import rs.ac.bg.fon.accommodationservice.dto.create.AccommodationDtoCreate;
import rs.ac.bg.fon.accommodationservice.dto.update.AccommodationDtoUpdate;
import rs.ac.bg.fon.accommodationservice.mapper.AccommodationMapper;
import rs.ac.bg.fon.accommodationservice.model.JwtReceiver.UserInfo;
import rs.ac.bg.fon.accommodationservice.service.AccommodationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccommodationDtoDomainAdapter {
    private final AccommodationService accService;
    private final AccommodationMapper accMapper;


    public Long save(AccommodationDtoCreate accommodationDto, UserInfo user) {
        return accService.save(accMapper.dtoCreateToDomain(accommodationDto, user.getId()), user);
    }

    public Long saveTry(AccommodationDtoCreate accommodationDtoCreate){
        return accService.saveTry(accMapper.dtoCreateToDomain(accommodationDtoCreate,1L));
    }


    public List<AccommodationDto> getAll(Pageable pageable) {
        return accService.getAll(pageable)
                .stream()
                .map(accMapper::domainToDto)
                .toList();
    }

    public AccommodationDto findById(Long id) {
        return accMapper.domainToDto(
                accService.findById(id));
    }

    public Long update(AccommodationDtoUpdate accommodationUpdate, UserInfo user) {
        return accService.update(accMapper.dtoUpdateToDomainUpdate(accommodationUpdate), user);
    }

    public void deleteById(Long id, UserInfo user) {
        accService.deleteById(id, user);
    }

    public void deleteById(Long id){
        accService.deleteById(id);
    }

    public List<AccommodationDto> getAllByHost(Long hostId, Pageable pageable) {
        return accService.getAllByHost(hostId, pageable)
                .stream()
                .map(accMapper::domainToDto)
                .toList();
    }

}
