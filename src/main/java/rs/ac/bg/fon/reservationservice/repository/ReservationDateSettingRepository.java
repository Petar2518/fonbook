package rs.ac.bg.fon.reservationservice.repository;

import rs.ac.bg.fon.reservationservice.model.ReservationDateSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReservationDateSettingRepository extends JpaRepository<ReservationDateSetting, Long> {
}