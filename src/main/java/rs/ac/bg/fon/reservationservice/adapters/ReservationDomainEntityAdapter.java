package rs.ac.bg.fon.reservationservice.adapters;
import rs.ac.bg.fon.reservationservice.domain.ReservationDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationDomainEntityAdapter {

    Long save(ReservationDomain reservationDomain);

    void update(ReservationDomain reservationDomain);
    Optional<ReservationDomain> getById(Long id);
    void delete(Long id);

    List<ReservationDomain> getAllExistingReservationsBetweenDates(LocalDate checkIn, LocalDate checkOut, Long accommodationUnitId);

    Page<ReservationDomain> getAllByProfileIdBetween( LocalDate checkIn, LocalDate checkOut, Long profileId, Pageable pageable);

    Page<ReservationDomain> getAll(Pageable pageable);

    Page<ReservationDomain> getAllByUnitsIdBetween(LocalDate checkIn, LocalDate checkOut, List<Long> ids, Pageable pageable);
}


