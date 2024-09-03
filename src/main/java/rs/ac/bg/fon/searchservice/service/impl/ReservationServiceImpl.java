package rs.ac.bg.fon.searchservice.service.impl;

import rs.ac.bg.fon.searchservice.domain.ReservationDomain;
import rs.ac.bg.fon.searchservice.mapper.ReservationMapper;
import rs.ac.bg.fon.searchservice.model.Reservation;
import rs.ac.bg.fon.searchservice.repository.ReservationRepository;
import rs.ac.bg.fon.searchservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper mapper;
    private final ReservationRepository reservationRepository;

    @Override
    public void save(ReservationDomain reservationDomain) {
        Reservation reservation = mapper.domainToEntity(reservationDomain);

        reservationRepository.save(reservation);
        log.info("Saved reservation {}", reservation);
    }

    @Override
    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
        log.info("Deleted reservation with id {}", id);
    }
}
