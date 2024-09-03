package rs.ac.bg.fon.accommodationservice.adapters.controllerServiceAdapters;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.dto.AddressDto;
import rs.ac.bg.fon.accommodationservice.dto.update.AddressDtoUpdate;
import rs.ac.bg.fon.accommodationservice.mapper.AddressMapper;
import rs.ac.bg.fon.accommodationservice.service.AddressService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AddressDtoDomainAdapter {
    private final AddressService addressService;
    private final AddressMapper addressMapper;


    public Long save(AddressDto addressDto) {
        return addressService.save(addressMapper.dtoToDomain(addressDto));
    }


    public List<AddressDto> getAll(Pageable pageable) {
        return addressService.getAll(pageable)
                .stream()
                .map(addressMapper::domainToDto)
                .toList();
    }

    public AddressDto findById(Long id) {
        return addressMapper.domainToDto(
                addressService.findById(id));
    }

    public Long update(AddressDtoUpdate addressDtoUpdate) {
        return addressService.update(addressMapper.dtoUpdateToDomainUpdate(addressDtoUpdate));
    }

    public void deleteById(Long id) {
        addressService.deleteById(id);
    }
}
