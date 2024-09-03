package rs.ac.bg.fon.accommodationservice.mapper;

import org.mapstruct.Mapper;
import rs.ac.bg.fon.accommodationservice.domain.ImageDomain;
import rs.ac.bg.fon.accommodationservice.dto.ImageDto;
import rs.ac.bg.fon.accommodationservice.model.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageDto domainToDto(ImageDomain imageDomain);

    ImageDomain dtoToDomain(ImageDto imageDto);

    ImageDomain entityToDomain(Image image);

    Image domainToEntity(ImageDomain imageDomain);
}
