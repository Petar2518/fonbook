package rs.ac.bg.fon.accommodationservice.controller;

import rs.ac.bg.fon.accommodationservice.adapters.controllerServiceAdapters.AccommodationDtoDomainAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.accommodationservice.dto.AccommodationDto;
import rs.ac.bg.fon.accommodationservice.dto.create.AccommodationDtoCreate;
import rs.ac.bg.fon.accommodationservice.dto.update.AccommodationDtoUpdate;
import rs.ac.bg.fon.accommodationservice.model.JwtReceiver.UserInfo;
import rs.ac.bg.fon.accommodationservice.repository.AccommodationRepository;
import rs.ac.bg.fon.accommodationservice.util.HeaderHandler;

import java.util.List;

@RestController
@RequestMapping("accommodations")
@RequiredArgsConstructor
@Slf4j
public class AccommodationController {
    private final HeaderHandler headerHandler;
    private final AccommodationRepository accRepository;
    private final AccommodationDtoDomainAdapter accAdapter;

    @GetMapping
    public List<AccommodationDto> getAllByPage(@PageableDefault(sort = "id", page = 0, size = 10, direction = Sort.Direction.ASC) Pageable pageable) {
        return accAdapter.getAll(pageable);
    }

    @GetMapping("/{id}")
    public AccommodationDto findById(@PathVariable Long id) {
        return accAdapter.findById(id);
    }

    @GetMapping("/hosts/{hostId}")
    public List<AccommodationDto> getAllForHostByPage(
            @PageableDefault
                    (sort = "id",
                            page = 0,
                            size = 10,
                            direction = Sort.Direction.ASC)
            Pageable pageable,
            @PathVariable Long hostId) {
        return accAdapter.getAllByHost(hostId, pageable);

    }

    @GetMapping("/my-accommodations")
    public List<AccommodationDto> getAccommodationsForLoggedInHost(
            @PageableDefault
                    (sort = "id",
                            page = 0,
                            size = 10,
                            direction = Sort.Direction.ASC)
            Pageable pageable,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String userInfo) {
        UserInfo user = headerHandler.extractPayloadFromJwt(userInfo);
        return accAdapter.getAllByHost(user.getId(), pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long save(@RequestHeader(HttpHeaders.AUTHORIZATION) String userInfo, @Valid @RequestBody AccommodationDtoCreate accommodationDto) {
        UserInfo user = headerHandler.extractPayloadFromJwt(userInfo);
        return accAdapter.save(accommodationDto, user);
    }

    @PutMapping
    public Long updateAccommodation(@RequestHeader(HttpHeaders.AUTHORIZATION) String userInfo, @Valid @RequestBody AccommodationDtoUpdate accommodationUpdate) {
        UserInfo user = headerHandler.extractPayloadFromJwt(userInfo);
        return accAdapter.update(accommodationUpdate, user);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTry(@PathVariable Long id) {
        accAdapter.deleteById(id);
    }

    @PostMapping("save")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveTry(@Valid @RequestBody AccommodationDtoCreate accommodationDto) {
        return accAdapter.saveTry(accommodationDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader(HttpHeaders.AUTHORIZATION) String userInfo, @PathVariable Long id) {
        UserInfo user = headerHandler.extractPayloadFromJwt(userInfo);
        accAdapter.deleteById(id, user);
    }
}
