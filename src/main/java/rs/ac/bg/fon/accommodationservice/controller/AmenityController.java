package rs.ac.bg.fon.accommodationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.accommodationservice.adapters.controllerServiceAdapters.AmenityDtoDomainAdapter;
import rs.ac.bg.fon.accommodationservice.dto.AmenityDto;

import java.util.List;

@RestController
@RequestMapping("amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityDtoDomainAdapter amenityAdapter;

    @GetMapping
    public List<AmenityDto> getAllByPage(@PageableDefault(sort = "id", page = 0, size = 10, direction = Sort.Direction.ASC) Pageable pageable) {
        return amenityAdapter.getAll(pageable);
    }

    @GetMapping("/{id}")
    public AmenityDto findById(@PathVariable Long id) {
        return amenityAdapter.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long save(@Valid @RequestBody AmenityDto amenityDto) {
        return amenityAdapter.save(amenityDto);
    }

    @PutMapping
    public Long updateAmenity(@Valid @RequestBody AmenityDto amenityDto) {
        return amenityAdapter.update(amenityDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        amenityAdapter.deleteById(id);
    }
}
