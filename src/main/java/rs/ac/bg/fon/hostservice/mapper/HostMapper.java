package rs.ac.bg.fon.hostservice.mapper;

import rs.ac.bg.fon.hostservice.domain.HostDomain;
import rs.ac.bg.fon.hostservice.dto.HostDto;
import rs.ac.bg.fon.hostservice.model.Host;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HostMapper {

    HostDomain dtoToDomain(HostDto hostDto);

    HostDto domainToDto(HostDomain hostDomain);

    Host domainToEntity(HostDomain hostDomain);

    HostDomain entityToDomain(Host host);

    Host toEntity(HostDto hostDto);
    HostDto toDto(Host host);
    List<HostDto> toDtoList (List<Host> hosts);


    default Page<HostDto> domainToDtoPage(Page<HostDomain> domainPage) {
        return domainPage.map(this::domainToDto);
    }

}
