package rs.ac.bg.fon.searchservice.controller;

import rs.ac.bg.fon.searchservice.adapters.controllerService.AccommodationDtoDomainAdapter;
import rs.ac.bg.fon.searchservice.dto.AccommodationDto;
import rs.ac.bg.fon.searchservice.dto.SearchRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final AccommodationDtoDomainAdapter adapter;

    @GetMapping("/")
    public Page<AccommodationDto> searchAccommodation(@Valid SearchRequest searchRequest, Pageable pageable) {
        return adapter.findByQuery(searchRequest, pageable);
    }

}
