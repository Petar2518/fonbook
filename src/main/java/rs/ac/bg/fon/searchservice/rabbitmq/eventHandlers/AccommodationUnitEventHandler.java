package rs.ac.bg.fon.searchservice.rabbitmq.eventHandlers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import rs.ac.bg.fon.searchservice.domain.AccommodationDomain;
import rs.ac.bg.fon.searchservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.searchservice.dto.message.AccommodationUnitMessageDto;
import rs.ac.bg.fon.searchservice.dto.message.IdMessageDto;
import rs.ac.bg.fon.searchservice.mapper.AccommodationMapper;
import rs.ac.bg.fon.searchservice.mapper.AccommodationUnitMapper;
import rs.ac.bg.fon.searchservice.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccommodationUnitEventHandler implements DataChangedMessageEventHandler<AccommodationUnitMessageDto> {

    private final AccommodationUnitMapper accommodationUnitMapper;
    private final AccommodationService accommodationService;
    private final AccommodationMapper accommodationMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.failure.insert.exchange}")
    private String insertExchangeType;
    @Value("${rabbitmq.failure.insert.accommodationUnit.routing-key}")
    private String insertRoutingKey;
    @Value("${rabbitmq.failure.delete.exchange}")
    private String deleteFailureExchangeType;
    @Value("${rabbitmq.success.delete.exchange}")
    private String deleteSuccessExchangeType;
    @Value("${rabbitmq.failure.delete.accommodationUnit.routing-key}")
    private String deleteFailureRoutingKey;
    @Value("${rabbitmq.success.delete.accommodationUnit.routing-key}")
    private String deleteSuccessRoutingKey;

    @Override
    public void handleGenericInsertAndUpdate(AccommodationUnitMessageDto unit) {
        AccommodationUnitDomain unitDomain = accommodationUnitMapper.messageDtoToDomain(unit);
        try {
            AccommodationDomain accommodationDomain = accommodationMapper.messageDtoToDomain(unit.getAccommodation());
            accommodationService.addUnit(unitDomain, accommodationDomain.getId());
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(insertExchangeType, insertRoutingKey, unitDomain.getId());
        }
    }

    @Override
    public void handleDelete(IdMessageDto id) {
        try {
            accommodationService.deleteUnit(id.getId());
            rabbitTemplate.convertAndSend(deleteSuccessExchangeType, deleteSuccessRoutingKey, id);
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(deleteFailureExchangeType, deleteFailureRoutingKey, id);
        }
    }

    @Override
    public Class<AccommodationUnitMessageDto> getEntityType() {
        return AccommodationUnitMessageDto.class;
    }
}
