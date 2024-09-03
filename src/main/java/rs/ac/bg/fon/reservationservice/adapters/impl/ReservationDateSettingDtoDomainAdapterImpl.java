package rs.ac.bg.fon.reservationservice.adapters.impl;


import rs.ac.bg.fon.reservationservice.adapters.ReservationDateSettingDtoDomainAdapter;
import rs.ac.bg.fon.reservationservice.dto.CreateReservationDateSettingDto;
import rs.ac.bg.fon.reservationservice.dto.ReservationDateSettingDto;
import rs.ac.bg.fon.reservationservice.mapper.ReservationDateSettingMapper;
import rs.ac.bg.fon.reservationservice.model.RequestStatus;
import rs.ac.bg.fon.reservationservice.service.ReservationDateSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ReservationDateSettingDtoDomainAdapterImpl implements ReservationDateSettingDtoDomainAdapter {

    private final ReservationDateSettingMapper reservationDateSettingMapper;
    private final ReservationDateSettingService reservationDateSettingService;


    @Override
    public Long save(Long reservationId, CreateReservationDateSettingDto createReservationDateSettingDto) {
        return reservationDateSettingService
                .save(reservationId, reservationDateSettingMapper
                        .fromCreateDtoToDomain(createReservationDateSettingDto));
    }

    @Override
    public ReservationDateSettingDto getById(Long id) {
        return reservationDateSettingMapper.fromDomainToDto(reservationDateSettingService.getById(id));
    }

    @Override
    public void changeStatusOfRequest(Long id, RequestStatus requestStatus) {
        reservationDateSettingService.changeStatusOfRequest(id,requestStatus);
    }
}
