package rs.ac.bg.fon.reservationservice.adapters.impl;

import rs.ac.bg.fon.reservationservice.adapters.ReservationDomainEntityAdapter;
import rs.ac.bg.fon.reservationservice.domain.ReservationDomain;
import rs.ac.bg.fon.reservationservice.mapper.ReservationMapper;
import rs.ac.bg.fon.reservationservice.model.Reservation;
import rs.ac.bg.fon.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservationDomainEntityAdapterImpl implements ReservationDomainEntityAdapter {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public Long save(ReservationDomain reservationDomain) {

        return reservationRepository
                .save(reservationMapper.fromDomainToEntity(reservationDomain)).getId();

    }

    @Override
    public void update(ReservationDomain reservationDomain) {
        reservationRepository
                .save(reservationMapper.fromDomainToEntity(reservationDomain));
    }

    @Override
    public Optional<ReservationDomain> getById(Long id) {
        return reservationRepository
                .findById(id)
                .map(reservationMapper::fromEntityToDomain);
    }

    @Override
    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }

    @Override
    public Page<ReservationDomain> getAll(Pageable pageable) {

        Page<Reservation> reservationPage = reservationRepository.findAll(pageable);
        return reservationMapper.fromPageEntityToPageDomain(reservationPage);

    }

    @Override
    public List<ReservationDomain> getAllExistingReservationsBetweenDates(LocalDate checkIn, LocalDate checkOut, Long accommodationUnitId) {
        return reservationMapper.fromEntityListToDomainList(reservationRepository
                .findAllExistingReservationsForDates(checkIn, checkOut, accommodationUnitId));
    }

    @Override
    public Page<ReservationDomain> getAllByProfileIdBetween(LocalDate checkIn, LocalDate checkOut, Long profileId, Pageable pageable) {
        return reservationMapper.fromPageEntityToPageDomain(reservationRepository
                .findReservationsBetweenDateRangeUsingProfileId(checkIn, checkOut, profileId, pageable));
    }

    @Override
    public Page<ReservationDomain> getAllByUnitsIdBetween(LocalDate checkIn, LocalDate checkOut, List<Long> ids, Pageable pageable) {
        return reservationMapper.fromPageEntityToPageDomain(reservationRepository
                .findReservationsBetweenDateRangeUsingUnitIds(checkIn, checkOut, ids, pageable));
    }
}

