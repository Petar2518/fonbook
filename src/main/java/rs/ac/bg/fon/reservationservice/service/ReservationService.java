package rs.ac.bg.fon.reservationservice.service;

import rs.ac.bg.fon.reservationservice.domain.ReservationDomain;
import rs.ac.bg.fon.reservationservice.domain.SearchReservationDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReservationService {

    Long save(ReservationDomain reservationDomain);

    ReservationDomain getById(Long id);

    void delete(Long id);

    Page<ReservationDomain> getAllByProfileId(SearchReservationDomain searchReservationDomain, String jwt);

    Page<ReservationDomain> getAll(Pageable pageable);

    void cancelReservation(Long reservationId, Long userId);


    void processPayment(Long reservationId);
}

