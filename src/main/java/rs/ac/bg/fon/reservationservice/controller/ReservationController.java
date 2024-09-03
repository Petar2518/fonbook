package rs.ac.bg.fon.reservationservice.controller;

import rs.ac.bg.fon.reservationservice.adapters.ReservationDtoDomainAdapter;
import rs.ac.bg.fon.reservationservice.dto.CreateReservationDto;
import rs.ac.bg.fon.reservationservice.dto.ReservationDto;
import rs.ac.bg.fon.reservationservice.dto.SearchReservationDto;
import rs.ac.bg.fon.reservationservice.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${spring.cors.payment-service.allowed-origins}")
public class ReservationController {

    private final JwtUtil jwtUtil;
    private final ReservationDtoDomainAdapter reservationDtoDomainAdapter;

    @PostMapping("reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveReservation(@RequestBody @Valid CreateReservationDto createReservationDto, @RequestHeader("Authorization") String jwt) {
        Long userId = jwtUtil.getFromJwt(jwt).getId();
        return reservationDtoDomainAdapter.save(createReservationDto, userId);
    }

    @GetMapping("reservations/{reservationId}")
    public ReservationDto getReservationById(@PathVariable Long reservationId) {
        return reservationDtoDomainAdapter.getReservationById(reservationId);
    }

    @DeleteMapping("reservations/{reservationId}")
    public void deleteReservations(@PathVariable Long reservationId) {
        reservationDtoDomainAdapter.deleteReservation(reservationId);
    }

    @GetMapping("reservations")
    public Page<ReservationDto> getAllReservations(Pageable pageable) {
        return reservationDtoDomainAdapter.getAll(pageable);
    }

    @GetMapping("/my-reservations")
    public Page<ReservationDto> getAllReservationsByProfileId(
            SearchReservationDto searchReservationDto,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt
    ) {
        return reservationDtoDomainAdapter.getAllByProfileId(searchReservationDto, jwt);
    }

    @PutMapping("reservations/{reservationId}/cancel")
    public void cancelReservation(@PathVariable Long reservationId, @RequestHeader("Authorization") String jwt) {
        Long userId = jwtUtil.getFromJwt(jwt).getId();
        reservationDtoDomainAdapter.cancelReservation(reservationId, userId);
    }

    @PatchMapping("reservations/{reservationId}/pay")
    public void processPayment(@PathVariable Long reservationId) {
        reservationDtoDomainAdapter.processPayment(reservationId);
        log.info("Reservation payment processed successfully for reservation ID: {}", reservationId);
    }

}

