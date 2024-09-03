package rs.ac.bg.fon.searchservice.rabbitmq.eventHandlers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import rs.ac.bg.fon.searchservice.domain.AccommodationDomain;
import rs.ac.bg.fon.searchservice.domain.AddressDomain;
import rs.ac.bg.fon.searchservice.dto.message.AddressMessageDto;
import rs.ac.bg.fon.searchservice.dto.message.IdMessageDto;
import rs.ac.bg.fon.searchservice.mapper.AccommodationMapper;
import rs.ac.bg.fon.searchservice.mapper.AddressMapper;
import rs.ac.bg.fon.searchservice.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressEventHandler implements DataChangedMessageEventHandler<AddressMessageDto> {

    private final AddressMapper addressMapper;
    private final AccommodationService accommodationService;
    private final AccommodationMapper accommodationMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.failure.insert.exchange}")
    private String insertExchangeType;
    @Value("${rabbitmq.failure.insert.address.routing-key}")
    private String insertRoutingKey;

    @Value("${rabbitmq.failure.delete.exchange}")
    private String deleteFailureExchangeType;
    @Value("${rabbitmq.success.delete.exchange}")
    private String deleteSuccessExchangeType;
    @Value("${rabbitmq.failure.delete.address.routing-key}")
    private String deleteFailureRoutingKey;
    @Value("${rabbitmq.success.delete.address.routing-key}")
    private String deleteSuccessRoutingKey;

    @Override
    public void handleGenericInsertAndUpdate(AddressMessageDto addressMessageDto) {
        AddressDomain addressDomain = addressMapper.messageDtoToDomain(addressMessageDto);
        try {
            AccommodationDomain accommodationDomain = accommodationMapper.messageDtoToDomain(addressMessageDto.getAccommodation());
            accommodationService.addAddress(addressDomain, accommodationDomain.getId());
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(insertExchangeType, insertRoutingKey, addressDomain.getId());
        }
    }

    @Override
    public void handleDelete(IdMessageDto id) {
        try {
            accommodationService.deleteAddress(id.getId());
            rabbitTemplate.convertAndSend(deleteSuccessExchangeType, deleteSuccessRoutingKey, id);
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(deleteFailureExchangeType, deleteFailureRoutingKey, id);
        }
    }

    @Override
    public Class<AddressMessageDto> getEntityType() {
        return AddressMessageDto.class;
    }
}
