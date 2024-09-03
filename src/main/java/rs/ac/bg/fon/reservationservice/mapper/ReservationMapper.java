package rs.ac.bg.fon.reservationservice.mapper;

import rs.ac.bg.fon.reservationservice.domain.ReservationDomain;
import rs.ac.bg.fon.reservationservice.domain.SearchReservationDomain;
import rs.ac.bg.fon.reservationservice.dto.CreateReservationDto;
import rs.ac.bg.fon.reservationservice.dto.ReservationDto;
import rs.ac.bg.fon.reservationservice.dto.SearchReservationDto;
import rs.ac.bg.fon.reservationservice.dto.message.ReservationEmailMessage;
import rs.ac.bg.fon.reservationservice.model.Reservation;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationDomain fromCreateReservationDtoToDomain(CreateReservationDto createReservationDto, Long profileId);

    @Named("fromDomainToReservationDto")
    ReservationDto fromDomainToReservationDto(ReservationDomain reservationDomain);

    @Named("fromDomainToEntity")
    @Mappings({
            @Mapping(source = "dateRange.checkInDate", target = "checkInDate"),
            @Mapping(source = "dateRange.checkOutDate", target = "checkOutDate")
    })
    Reservation fromDomainToEntity(ReservationDomain reservationDomain);

    @Named("fromEntityToDomain")
    @Mappings({
            @Mapping(source = "checkInDate", target = "dateRange.checkInDate"),
            @Mapping(source = "checkOutDate", target = "dateRange.checkOutDate"),
    })
    ReservationDomain fromEntityToDomain(Reservation reservation);

    
    @Mapping(source = "email", target = "email")
    ReservationEmailMessage fromEntityToReservationEmailMessage(Reservation reservation, String email);

    default ReservationDto fromEntityToReservationDto(Reservation reservation) {
        ReservationDomain reservationDomain = this.fromEntityToDomain(reservation);
        return fromDomainToReservationDto(reservationDomain);
    }


    ReservationDomain fromDtoToDomain(ReservationDto reservationDto);

    ReservationDto fromDomainToDto(ReservationDomain reservationDomain);

    default Page<ReservationDto> fromDomainToDtoPage(Page<ReservationDomain> domainPage) {
        return domainPage.map(this::fromDomainToDto);
    }

    @IterableMapping(qualifiedByName = "fromEntityToDomain")
    List<ReservationDomain> fromEntityListToDomainList(List<Reservation> reservations);

    SearchReservationDomain fromSearchDtoToDomain(SearchReservationDto searchReservationDto);

    default Page<ReservationDomain> fromPageEntityToPageDomain(Page<Reservation> reservationPage) {
        List<ReservationDomain> reservationDomainList = fromEntityListToDomainList(reservationPage.getContent());
        return new PageImpl<>(reservationDomainList, reservationPage.getPageable(), reservationPage.getTotalElements());
    }


}

