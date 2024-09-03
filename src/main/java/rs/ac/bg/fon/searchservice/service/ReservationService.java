package rs.ac.bg.fon.searchservice.service;

import rs.ac.bg.fon.searchservice.domain.ReservationDomain;

public interface ReservationService {

    void save(ReservationDomain reservationDomain);

    void deleteById(Long id);
}
