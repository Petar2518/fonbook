package rs.ac.bg.fon.reservationservice.adapters;

import rs.ac.bg.fon.reservationservice.dto.CreateReservationDateSettingDto;
import rs.ac.bg.fon.reservationservice.dto.ReservationDateSettingDto;
import rs.ac.bg.fon.reservationservice.model.RequestStatus;

public interface ReservationDateSettingDtoDomainAdapter {

    Long save(Long reservationId, CreateReservationDateSettingDto createReservationDateSettingDto);

    ReservationDateSettingDto getById(Long id);

    void changeStatusOfRequest(Long id, RequestStatus requestStatus);


}
