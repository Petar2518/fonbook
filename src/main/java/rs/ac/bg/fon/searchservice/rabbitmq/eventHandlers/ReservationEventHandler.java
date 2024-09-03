package rs.ac.bg.fon.searchservice.rabbitmq.eventHandlers;

import rs.ac.bg.fon.searchservice.domain.ReservationDomain;
import rs.ac.bg.fon.searchservice.dto.message.IdMessageDto;
import rs.ac.bg.fon.searchservice.dto.message.ReservationMessageDto;
import rs.ac.bg.fon.searchservice.mapper.ReservationMapper;
import rs.ac.bg.fon.searchservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventHandler implements DataChangedMessageEventHandler<ReservationMessageDto> {

    private final ReservationMapper reservationMapper;
    private final ReservationService reservationService;

    @Override
    public void handleGenericInsertAndUpdate(ReservationMessageDto messageDto) {
        ReservationDomain reservationDomain = reservationMapper.messageDtoToDomain(messageDto);
        reservationService.save(reservationDomain);
    }

    @Override
    public void handleDelete(IdMessageDto id) {
        reservationService.deleteById(1L);
    }

    @Override
    public Class<ReservationMessageDto> getEntityType() {
        return ReservationMessageDto.class;
    }
}
