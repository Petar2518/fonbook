package rs.ac.bg.fon.reservationservice.controller;


import rs.ac.bg.fon.reservationservice.adapters.ReservationDateSettingDtoDomainAdapter;
import rs.ac.bg.fon.reservationservice.dto.CreateReservationDateSettingDto;
import rs.ac.bg.fon.reservationservice.dto.ReservationDateSettingDto;
import rs.ac.bg.fon.reservationservice.model.RequestStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReservationDateSettingController {

    private final ReservationDateSettingDtoDomainAdapter reservationDateSettingDtoDomainAdapter;

    @PostMapping("reservations/{reservationId}/pendingRequests")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createPendingRequestToHost(@PathVariable Long reservationId, @RequestBody @Valid CreateReservationDateSettingDto createReservationDateSettingDto) {
        return reservationDateSettingDtoDomainAdapter.save(reservationId, createReservationDateSettingDto);
    }

    @GetMapping("reservations/{reservationId}/pendingRequests")
    public ReservationDateSettingDto getById(@PathVariable Long reservationId) {
        return reservationDateSettingDtoDomainAdapter.getById(reservationId);
    }

    @PutMapping("reservations/{reservationId}/pendingRequests")
    public void changeRequestStatus(@PathVariable Long reservationId, @RequestParam RequestStatus requestStatus) {
        reservationDateSettingDtoDomainAdapter.changeStatusOfRequest(reservationId, requestStatus);
    }

}
