package rs.ac.bg.fon.reservationservice.adapters;


import rs.ac.bg.fon.reservationservice.domain.ReservationDateSettingDomain;

import java.util.Optional;

public interface ReservationDateSettingDomainEntityAdapter {

    Long save(ReservationDateSettingDomain reservationDateSettingDomain);

    Optional<ReservationDateSettingDomain> getById(Long id);

    void deleteById(Long id);


}
