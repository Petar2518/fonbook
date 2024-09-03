package rs.ac.bg.fon.reservationservice.service;

import rs.ac.bg.fon.reservationservice.domain.ReservationDateSettingDomain;
import rs.ac.bg.fon.reservationservice.model.RequestStatus;

public interface ReservationDateSettingService {

    Long save(Long reservationId, ReservationDateSettingDomain reservationDateSettingDomain);

    ReservationDateSettingDomain getById(Long id);

    void changeStatusOfRequest(Long id, RequestStatus requestStatus);

}
