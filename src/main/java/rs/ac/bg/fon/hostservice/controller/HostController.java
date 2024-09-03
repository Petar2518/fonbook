package rs.ac.bg.fon.hostservice.controller;

import rs.ac.bg.fon.hostservice.adapters.HostDtoDomainAdapter;
import rs.ac.bg.fon.hostservice.dto.HostDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hosts")
public class HostController {


    private final HostDtoDomainAdapter dtoDomainAdapter;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHost(@RequestBody @Valid HostDto hostDto) {
         dtoDomainAdapter.save(hostDto);
    }

    @GetMapping("/{id}")
    public HostDto getHostById(@PathVariable("id") Long id) {
        return dtoDomainAdapter.getById(id);
    }

    @PostMapping("/activate/{id}")
    public void activateHostById(@PathVariable("id") Long id){
        dtoDomainAdapter.activateById(id);
    }
    @GetMapping
    public Page<HostDto> getAll(Pageable pageable) {
        return dtoDomainAdapter.getAll(pageable);
    }
    @PutMapping
    public void updateHost(@RequestBody @Valid HostDto hostDto){
         dtoDomainAdapter.update(hostDto);
    }

    @DeleteMapping("/{id}")
    public void deleteHost(@PathVariable("id") Long id){
        dtoDomainAdapter.delete(id);
    }

}
