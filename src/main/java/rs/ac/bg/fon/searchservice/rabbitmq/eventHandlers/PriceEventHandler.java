package rs.ac.bg.fon.searchservice.rabbitmq.eventHandlers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import rs.ac.bg.fon.searchservice.domain.PriceDomain;
import rs.ac.bg.fon.searchservice.dto.message.IdMessageDto;
import rs.ac.bg.fon.searchservice.dto.message.PriceMessageDto;
import rs.ac.bg.fon.searchservice.mapper.PriceMapper;
import rs.ac.bg.fon.searchservice.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceEventHandler implements DataChangedMessageEventHandler<PriceMessageDto> {

    private final PriceService priceService;
    private final PriceMapper priceMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.failure.insert.exchange}")
    private String insertExchangeType;
    @Value("${rabbitmq.failure.insert.price.routing-key}")
    private String insertRoutingKey;

    @Value("${rabbitmq.failure.delete.exchange}")
    private String deleteFailureExchangeType;
    @Value("${rabbitmq.success.delete.exchange}")
    private String deleteSuccessExchangeType;
    @Value("${rabbitmq.failure.delete.price.routing-key}")
    private String deleteFailureRoutingKey;
    @Value("${rabbitmq.success.delete.price.routing-key}")
    private String deleteSuccessRoutingKey;

    @Override
    public void handleGenericInsertAndUpdate(PriceMessageDto priceMessageDto) {
        PriceDomain priceDomain = priceMapper.messageDtoToDomain(priceMessageDto);
        try {
            priceService.save(priceDomain);
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(insertExchangeType, insertRoutingKey, priceDomain.getId());
        }
    }

    @Override
    public void handleDelete(IdMessageDto id) {
        try {
            priceService.deleteById(id.getId());
            rabbitTemplate.convertAndSend(deleteSuccessExchangeType, deleteSuccessRoutingKey, id);
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(deleteFailureExchangeType, deleteFailureRoutingKey, id);
        }
    }

    @Override
    public Class<PriceMessageDto> getEntityType() {
        return PriceMessageDto.class;
    }
}
