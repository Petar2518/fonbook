package rs.ac.bg.fon.searchservice.adapters.controllerService.impl;

import rs.ac.bg.fon.searchservice.adapters.controllerService.AccommodationDtoDomainAdapter;
import rs.ac.bg.fon.searchservice.dto.AccommodationDto;
import rs.ac.bg.fon.searchservice.mapper.AccommodationMapper;
import rs.ac.bg.fon.searchservice.service.SearchService;
import rs.ac.bg.fon.searchservice.dto.SearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccommodationDtoDomainAdapterImpl implements AccommodationDtoDomainAdapter {

    private final AccommodationMapper accommodationMapper;
    private final SearchService searchService;

    @Override
    public Page<AccommodationDto> findByQuery(SearchRequest searchRequest, Pageable pageable) {
        return accommodationMapper.domainsToDtos(searchService.findByQuery(searchRequest, pageable));
    }

}
