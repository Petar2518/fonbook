package rs.ac.bg.fon.accommodationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.accommodationservice.adapters.controllerServiceAdapters.AccommodationUnitDtoDomainAdapter;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationUnitDto;
import rs.ac.bg.fon.accommodationservice.dto.update.AccommodationUnitDtoUpdate;
import rs.ac.bg.fon.accommodationservice.model.JwtReceiver.UserInfo;
import rs.ac.bg.fon.accommodationservice.util.HeaderHandler;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccommodationUnitController {

    private final HeaderHandler headerHandler;
    private final AccommodationUnitDtoDomainAdapter accUnitAdapter;

    @GetMapping("accommodations/{accommodationId}/rooms")
    public List<AccommodationUnitDto> getAllByPage(
            @PathVariable Long accommodationId,
            @PageableDefault(sort = "capacity", page = 0, size = 10, direction = Sort.Direction.DESC) Pageable pageable) {
        return accUnitAdapter.getAll(accommodationId, pageable);
    }

    @GetMapping("/rooms/{id}")
    public AccommodationUnitDto findById(@PathVariable Long id) {
        return accUnitAdapter.findById(id);
    }

    @GetMapping("/rooms/my-rooms")
    public List<AccommodationUnitDto> findByHostId(
            @PageableDefault
                    (sort = "id",
                            page = 0,
                            size = 10,
                            direction = Sort.Direction.ASC)
            Pageable pageable,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String userInfo
    ) {
        UserInfo user = headerHandler.extractPayloadFromJwt(userInfo);
        return accUnitAdapter.getAllByHost(user.getId(), pageable);
    }

    @PostMapping("/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public Long save(@Valid @RequestBody AccommodationUnitDto accommodationUnitDto) {
        return accUnitAdapter.save(accommodationUnitDto);
    }

    @PutMapping("/rooms")
    public Long updateAccommodationUnit(@Valid @RequestBody AccommodationUnitDtoUpdate accommodationUnitUpdate) {
        return accUnitAdapter.update(accommodationUnitUpdate);
    }

    @DeleteMapping("/rooms/{id}")
    public void delete(@PathVariable Long id) {
        accUnitAdapter.deleteById(id);
    }
}
