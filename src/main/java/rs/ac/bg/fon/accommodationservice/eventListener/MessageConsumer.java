package rs.ac.bg.fon.accommodationservice.eventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.dto.message.IdMessageDto;
import rs.ac.bg.fon.accommodationservice.repository.AccommodationRepository;
import rs.ac.bg.fon.accommodationservice.repository.AccommodationUnitRepository;
import rs.ac.bg.fon.accommodationservice.repository.AddressRepository;
import rs.ac.bg.fon.accommodationservice.repository.PriceRepository;
import rs.ac.bg.fon.accommodationservice.service.AccommodationService;
import rs.ac.bg.fon.accommodationservice.service.AccommodationUnitService;
import rs.ac.bg.fon.accommodationservice.service.AddressService;
import rs.ac.bg.fon.accommodationservice.service.PriceService;


@Component
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationUnitRepository accommodationUnitRepository;
    private final AddressRepository addressRepository;
    private final PriceRepository priceRepository;
    private final AccommodationUnitService accommodationUnitService;
    private final AccommodationService accommodationService;
    private final AddressService addressService;
    private final PriceService priceService;

    @RabbitListener(queues = "${rabbitmq.failure.insert.queues.accommodation}")
    public void accommodationTransactionFailed(Long accommodationId) {
        log.info("Consumed accommodation with id: {} from queue", accommodationId);
        accommodationRepository.deleteById(accommodationId);
    }

    @RabbitListener(queues = "${rabbitmq.failure.insert.queues.accommodation-unit}")
    public void accommodationUnitTransactionFailed(Long accommodationUnitId) {
        log.info("Consumed accommodation unit with id: {} from queue", accommodationUnitId);
        accommodationUnitRepository.deleteById(accommodationUnitId);
    }

    @RabbitListener(queues = "${rabbitmq.failure.insert.queues.price}")
    public void priceTransactionFailed(Long priceId) {
        log.info("Consumed price with id: {} from queue", priceId);
        priceRepository.deleteById(priceId);
    }

    @RabbitListener(queues = "${rabbitmq.failure.insert.queues.address}")
    public void addressTransactionFailed(Long addressId) {
        log.info("Consumed address with id: {} from queue", addressId);
        addressRepository.deleteById(addressId);
    }


    @RabbitListener(queues = "${rabbitmq.failure.delete.queues.accommodation}")
    public void accommodationDeletionFailed(IdMessageDto accommodationId) {
        log.info("Consumed accommodation with id: {} from queue for reverting deletion", accommodationId.getId());
        accommodationService.revertDelete(accommodationId.getId());
    }

    @RabbitListener(queues = "${rabbitmq.failure.delete.queues.accommodation-unit}")
    public void accommodationUnitDeletionFailed(IdMessageDto accommodationUnitId) {
        log.info("Consumed accommodation unit with id: {} from queue for reverting deletion", accommodationUnitId.getId());
       accommodationUnitService.revertDelete(accommodationUnitId.getId());
    }

    @RabbitListener(queues = "${rabbitmq.failure.delete.queues.address}")
    public void addressDeletionFailed(IdMessageDto addressId) {
        log.info("Consumed address with id: {} from queue for reverting deletion", addressId.getId());
        addressService.revertDelete(addressId.getId());
    }

    @RabbitListener(queues = "${rabbitmq.failure.delete.queues.price}")
    public void priceDeletionFailed(IdMessageDto priceId) {
        log.info("Consumed price with id: {} from queue for reverting deletion", priceId.getId());
        priceService.revertDelete(priceId.getId());

    }
}
