package rs.ac.bg.fon.reservationservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import rs.ac.bg.fon.reservationservice.adapters.ReservationDateSettingDomainEntityAdapter;
import rs.ac.bg.fon.reservationservice.domain.ReservationDateSettingDomain;
import rs.ac.bg.fon.reservationservice.domain.ReservationDomain;
import rs.ac.bg.fon.reservationservice.exceptions.InvalidRequestException;
import rs.ac.bg.fon.reservationservice.exceptions.RequestAlreadyResolvedException;
import rs.ac.bg.fon.reservationservice.exceptions.ResourceNotFoundException;
import rs.ac.bg.fon.reservationservice.model.RequestStatus;
import rs.ac.bg.fon.reservationservice.service.ReservationDateSettingService;
import rs.ac.bg.fon.reservationservice.service.ReservationService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Builder
@RequiredArgsConstructor
public class ReservationDateSettingServiceImpl implements ReservationDateSettingService {

    private final ReservationDateSettingDomainEntityAdapter reservationDateSettingDomainEntityAdapter;
    private final ReservationService reservationService;
    private final ObjectMapper objectMapper;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void changeStatusOfRequest(Long id, RequestStatus requestStatus) {

        ReservationDateSettingDomain reservationDateSettingDomain = getById(id);

        validateStatus(reservationDateSettingDomain, requestStatus);

        if (requestStatus == RequestStatus.CONFIRMED) {
            updateReservation(reservationDateSettingDomain, id);
        }

        reservationDateSettingDomainEntityAdapter.deleteById(id);
    }

    @Override
    public ReservationDateSettingDomain getById(Long id) {
        return reservationDateSettingDomainEntityAdapter.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation date setting", "id", id));
    }

    public void updateReservation(ReservationDateSettingDomain reservationDateSettingDomain, Long reservationId) {
        ReservationDomain reservationDomain = reservationService.getById(reservationId);

        reservationDomain.setDateRange(reservationDateSettingDomain.getDateRange());
        reservationService.save(reservationDomain);
    }

    @Override
    public Long save(Long reservationId, ReservationDateSettingDomain reservationDateSettingDomain) {
        reservationDateSettingDomain.setId(reservationId);
        reservationDateSettingDomain.setStatus(RequestStatus.PENDING);
        return reservationDateSettingDomainEntityAdapter.save(reservationDateSettingDomain);
    }

    public void validateStatus(ReservationDateSettingDomain reservationDateSettingDomain, RequestStatus requestStatus) {
        if (!reservationDateSettingDomain.getStatus().equals(RequestStatus.PENDING)) {
            throw new RequestAlreadyResolvedException();
        }

        if (!(requestStatus.equals(RequestStatus.CONFIRMED) || requestStatus.equals(RequestStatus.DENIED))) {
            throw new InvalidRequestException();
        }
    }
}
