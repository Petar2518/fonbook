package rs.ac.bg.fon.bookinggateway.mapper;

import rs.ac.bg.fon.bookinggateway.dto.HostDetailsDto;
import rs.ac.bg.fon.bookinggateway.dto.RegistrationDto;
import rs.ac.bg.fon.bookinggateway.dto.UserDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RegistrationRequestMapper {
    UserDetailsDto fromRegistrationToUser(RegistrationDto registrationDto);

    HostDetailsDto fromRegistrationToHost(RegistrationDto registrationDto);
}
