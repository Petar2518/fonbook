package rs.ac.bg.fon.searchservice.adapters.controllerService;

import rs.ac.bg.fon.searchservice.dto.AccommodationDto;
import rs.ac.bg.fon.searchservice.dto.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccommodationDtoDomainAdapter {

    Page<AccommodationDto> findByQuery(SearchRequest searchRequest, Pageable pageable);

}
