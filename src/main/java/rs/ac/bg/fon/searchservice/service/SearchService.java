package rs.ac.bg.fon.searchservice.service;


import rs.ac.bg.fon.searchservice.domain.AccommodationDomain;
import rs.ac.bg.fon.searchservice.dto.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {

    public Page<AccommodationDomain> findByQuery(SearchRequest searchRequest, Pageable pageable);

}
