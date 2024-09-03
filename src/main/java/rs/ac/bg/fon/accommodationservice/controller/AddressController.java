package rs.ac.bg.fon.accommodationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.accommodationservice.adapters.controllerServiceAdapters.AddressDtoDomainAdapter;
import rs.ac.bg.fon.accommodationservice.dto.AddressDto;
import rs.ac.bg.fon.accommodationservice.dto.update.AddressDtoUpdate;

import java.util.List;

@RestController
@RequestMapping("addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressDtoDomainAdapter addressAdapter;

    @GetMapping
    public List<AddressDto> getAllByPage(@PageableDefault(sort = "id", page = 0, size = 10, direction = Sort.Direction.ASC) Pageable pageable) {
        return addressAdapter.getAll(pageable);
    }

    @GetMapping("/{id}")
    public AddressDto findById(@PathVariable Long id) {
        return addressAdapter.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long save(@Valid @RequestBody AddressDto addressDto) {
        return addressAdapter.save(addressDto);
    }

    @PutMapping
    public Long updateAddress(@Valid @RequestBody AddressDtoUpdate addressDto) {
        return addressAdapter.update(addressDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        addressAdapter.deleteById(id);
    }
}
