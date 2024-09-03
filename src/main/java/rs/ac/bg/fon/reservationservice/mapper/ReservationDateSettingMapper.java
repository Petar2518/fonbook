package rs.ac.bg.fon.reservationservice.mapper;


import rs.ac.bg.fon.reservationservice.domain.ReservationDateSettingDomain;
import rs.ac.bg.fon.reservationservice.dto.CreateReservationDateSettingDto;
import rs.ac.bg.fon.reservationservice.dto.ReservationDateSettingDto;
import rs.ac.bg.fon.reservationservice.model.ReservationDateSetting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = ReservationMapper.class)
public interface ReservationDateSettingMapper {

    ReservationDateSettingDomain fromCreateDtoToDomain(CreateReservationDateSettingDto createReservationDateSettingDto);

    @Mappings({
            @Mapping(source = "id",target = "reservation.id"),
            @Mapping(source = "dateRange.checkInDate", target = "updatedCheckIn"),
            @Mapping(source = "dateRange.checkOutDate", target = "updatedCheckOut"),
    })
    ReservationDateSetting fromDomainToEntity(ReservationDateSettingDomain reservationDateSettingDomain);

    ReservationDateSettingDto fromDomainToDto(ReservationDateSettingDomain reservationDateSettingDomain);

    @Named("fromEntityToDomain")
    @Mappings({
            @Mapping(source = "updatedCheckIn", target = "dateRange.checkInDate"),
            @Mapping(source = "updatedCheckOut", target = "dateRange.checkOutDate"),
    })
    ReservationDateSettingDomain fromEntityToDomain(ReservationDateSetting reservationDateSetting);

}
