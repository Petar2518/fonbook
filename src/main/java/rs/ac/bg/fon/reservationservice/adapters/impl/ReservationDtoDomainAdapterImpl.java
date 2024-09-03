package rs.ac.bg.fon.reservationservice.adapters.impl;

import rs.ac.bg.fon.reservationservice.adapters.ReservationDtoDomainAdapter;
import rs.ac.bg.fon.reservationservice.domain.SearchReservationDomain;
import rs.ac.bg.fon.reservationservice.dto.CreateReservationDto;
import rs.ac.bg.fon.reservationservice.dto.ReservationDto;
import rs.ac.bg.fon.reservationservice.dto.SearchReservationDto;
import rs.ac.bg.fon.reservationservice.mapper.ReservationMapper;
import rs.ac.bg.fon.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationDtoDomainAdapterImpl implements ReservationDtoDomainAdapter {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @Override
    public Long save(CreateReservationDto createReservationDto, Long userId) {
        return reservationService
                .save(reservationMapper
                        .fromCreateReservationDtoToDomain(createReservationDto, userId));
    }

    @Override
    public ReservationDto getReservationById(Long id) {
        return reservationMapper
                .fromDomainToReservationDto(reservationService
                        .getById(id));
    }

    @Override
    public void deleteReservation(Long id) {
        reservationService
                .delete(id);
    }

    @Override
    public Page<ReservationDto> getAllByProfileId(SearchReservationDto searchReservationDto, String jwt) {

        SearchReservationDomain searchReservationDomain = reservationMapper.fromSearchDtoToDomain(searchReservationDto);
        return reservationMapper
                .fromDomainToDtoPage(reservationService
                        .getAllByProfileId(searchReservationDomain, jwt));
    }

    @Override
    public Page<ReservationDto> getAll(Pageable pageable) {
        return reservationMapper
                .fromDomainToDtoPage(reservationService
                        .getAll(pageable));
    }

    @Override
    public void cancelReservation(Long reservationId, Long userId) {
        reservationService.cancelReservation(reservationId, userId);
    }

    @Override
    public void processPayment(Long reservationId) {
        reservationService.processPayment(reservationId);
    }
}

