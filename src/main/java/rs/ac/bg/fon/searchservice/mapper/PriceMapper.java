package rs.ac.bg.fon.searchservice.mapper;

import rs.ac.bg.fon.searchservice.domain.PriceDomain;
import rs.ac.bg.fon.searchservice.dto.message.PriceMessageDto;
import rs.ac.bg.fon.searchservice.model.Price;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceMapper {

    Price domainToEntity(PriceDomain priceDomain);

    PriceDomain entityToDomain(Price price);

    List<PriceDomain> entitiesToDomains(List<Price> prices);

    @Mapping(target = "accommodationUnitId", source = "priceMessageDto.accommodationUnit.id")
    PriceDomain messageDtoToDomain(PriceMessageDto priceMessageDto);
}
