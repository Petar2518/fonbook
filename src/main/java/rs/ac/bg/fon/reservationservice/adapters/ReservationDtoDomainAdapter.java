package rs.ac.bg.fon.reservationservice.adapters;

import rs.ac.bg.fon.reservationservice.dto.CreateReservationDto;
import rs.ac.bg.fon.reservationservice.dto.ReservationDto;
import rs.ac.bg.fon.reservationservice.dto.SearchReservationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationDtoDomainAdapter {

    Long save(CreateReservationDto createReservationDto, Long userId);

    ReservationDto getReservationById(Long id);

    void deleteReservation(Long id);

    Page<ReservationDto> getAllByProfileId(SearchReservationDto searchReservationDto, String jwt);

    Page<ReservationDto> getAll(Pageable pageable);

    void cancelReservation(Long reservationId, Long userId);

    void processPayment(Long reservationId);

}
