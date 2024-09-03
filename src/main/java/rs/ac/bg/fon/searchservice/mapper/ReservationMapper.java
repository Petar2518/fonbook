package rs.ac.bg.fon.searchservice.mapper;

import rs.ac.bg.fon.searchservice.domain.ReservationDomain;
import rs.ac.bg.fon.searchservice.dto.message.ReservationMessageDto;
import rs.ac.bg.fon.searchservice.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    Reservation domainToEntity(ReservationDomain reservationDomain);

    ReservationDomain entityToDomain(Reservation reservation);

    List<ReservationDomain> entitiesToDomains(List<Reservation> reservations);

    @Mapping(target = "reservationStatus", source = "status")
    @Mapping(target = "checkInDate", source = "reservationMessageDto.dateRange.checkInDate")
    @Mapping(target = "checkOutDate", source = "reservationMessageDto.dateRange.checkOutDate")
    ReservationDomain messageDtoToDomain(ReservationMessageDto reservationMessageDto);

}
