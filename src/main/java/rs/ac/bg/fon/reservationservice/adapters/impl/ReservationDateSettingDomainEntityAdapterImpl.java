package rs.ac.bg.fon.reservationservice.adapters.impl;

import rs.ac.bg.fon.reservationservice.adapters.ReservationDateSettingDomainEntityAdapter;
import rs.ac.bg.fon.reservationservice.domain.ReservationDateSettingDomain;
import rs.ac.bg.fon.reservationservice.mapper.ReservationDateSettingMapper;
import rs.ac.bg.fon.reservationservice.repository.ReservationDateSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ReservationDateSettingDomainEntityAdapterImpl implements ReservationDateSettingDomainEntityAdapter {

    private final ReservationDateSettingRepository reservationDateSettingRepository;
    private final ReservationDateSettingMapper reservationDateSettingMapper;

    @Override
    public Long save(ReservationDateSettingDomain reservationDateSettingDomain) {
        return reservationDateSettingRepository
                .save(reservationDateSettingMapper.fromDomainToEntity(reservationDateSettingDomain))
                .getId();
    }

    @Override
    public Optional<ReservationDateSettingDomain> getById(Long id) {

        return reservationDateSettingRepository
                .findById(id)
                .map(reservationDateSettingMapper::fromEntityToDomain);
    }

    @Override
    public void deleteById(Long id) {
        reservationDateSettingRepository.deleteById(id);
    }

}