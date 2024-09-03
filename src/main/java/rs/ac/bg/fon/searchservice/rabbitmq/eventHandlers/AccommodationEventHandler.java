package rs.ac.bg.fon.searchservice.rabbitmq.eventHandlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import rs.ac.bg.fon.searchservice.domain.AccommodationDomain;
import rs.ac.bg.fon.searchservice.dto.message.AccommodationMessageDto;
import rs.ac.bg.fon.searchservice.dto.message.IdMessageDto;
import rs.ac.bg.fon.searchservice.mapper.AccommodationMapper;
import rs.ac.bg.fon.searchservice.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class AccommodationEventHandler implements DataChangedMessageEventHandler<AccommodationMessageDto> {

    private final AccommodationService accommodationService;
    private final AccommodationMapper accommodationMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.failure.insert.exchange}")
    private String insertExchangeType;
    @Value("${rabbitmq.failure.insert.accommodation.routing-key}")
    private String insertRoutingKey;
    @Value("${rabbitmq.failure.delete.exchange}")
    private String deleteFailureExchangeType;
    @Value("${rabbitmq.success.delete.exchange}")
    private String deleteSuccessExchangeType;
    @Value("${rabbitmq.failure.delete.accommodation.routing-key}")
    private String deleteFailureRoutingKey;
    @Value("${rabbitmq.success.delete.accommodation.routing-key}")
    private String deleteSuccessRoutingKey;

    @Override
    public void handleGenericInsertAndUpdate(AccommodationMessageDto messageDto) {

        AccommodationDomain accommodationDomain = accommodationMapper.messageDtoToDomain(messageDto);
        try {

            accommodationService.save(accommodationDomain);
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(insertExchangeType, insertRoutingKey, accommodationDomain.getId());
        }
    }

//    @Override
//    public void handleGenericUpdate(AccommodationMessageDto messageDto) {
//
//        AccommodationDomain accommodationDomain = accommodationMapper.messageDtoToDomain(messageDto);
//        try {
//            accommodationService.save(accommodationDomain);
//        } catch (Exception e) {
//            rabbitTemplate.convertAndSend(exchangeType,routingKey,accommodationDomain.getId());
//        }
//    }

    @Override
    public void handleDelete(IdMessageDto id) {
        try {
            accommodationService.deleteById(id.getId());
            rabbitTemplate.convertAndSend(deleteSuccessExchangeType,deleteSuccessRoutingKey,id);
        }catch (Exception e){
            rabbitTemplate.convertAndSend(deleteFailureExchangeType,deleteFailureRoutingKey,id);
        }
    }

    @Override
    public Class<AccommodationMessageDto> getEntityType() {
        return AccommodationMessageDto.class;
    }
}
