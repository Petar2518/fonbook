package rs.ac.bg.fon.accommodationservice.service.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.PriceDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.domain.PriceDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.PriceDomainUpdate;
import rs.ac.bg.fon.accommodationservice.dto.dateFilter.PricesDate;
import rs.ac.bg.fon.accommodationservice.dto.message.IdMessageDto;
import rs.ac.bg.fon.accommodationservice.exception.specific.DateUnavailableException;
import rs.ac.bg.fon.accommodationservice.exception.specific.MapperException;
import rs.ac.bg.fon.accommodationservice.exception.specific.PriceNotFoundException;
import rs.ac.bg.fon.accommodationservice.service.PriceService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final PriceDomainEntityAdapter serviceRepositoryAdapter;
    private final ObjectMapper objectMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.start.delete.exchanges}")
    private String startTransactionExchange;

    @Value("${rabbitmq.start.delete.price.routing-key}")
    private String priceStartTransactionKey;

    @Override
    public Long save(PriceDomain priceDomain) {
        Long accUnit = priceDomain.getAccommodationUnit().getId();
        LocalDate dateFrom = priceDomain.getDateFrom();
        LocalDate dateTo = priceDomain.getDateTo();
        priceDomain.setDeleted(false);
        if (!serviceRepositoryAdapter.findPricesForDatesForAccommodationUnit(accUnit, dateFrom, dateTo).isEmpty()) {
            throw new DateUnavailableException();
        }
        return serviceRepositoryAdapter.save(priceDomain);
    }

    @Override
    public void deleteById(Long id) {
        try {
            PriceDomain existingPriceById = findById(id);
            existingPriceById.setDeleted(true);
            serviceRepositoryAdapter.save(existingPriceById);
            rabbitTemplate.convertAndSend(startTransactionExchange, priceStartTransactionKey, new IdMessageDto(id));

        }
        catch (Exception e){

        }
    }

    @Override
    public List<PriceDomain> getAll(Long accommodationId, PricesDate pricesDate) {

        LocalDate startDate;
        LocalDate endDate;

        if (pricesDate.getStartDate() == null) {
            startDate = LocalDate.now();
        } else {
            startDate = pricesDate.getStartDate();
        }
        if (pricesDate.getEndDate() == null) {
            endDate = LocalDate.of(2100, 1, 1);
        } else {
            endDate = pricesDate.getEndDate();
        }
        return serviceRepositoryAdapter.findPricesForDatesForAccommodationUnit(accommodationId, startDate, endDate);
    }


    @Override
    public Long update(PriceDomainUpdate priceDomain) {
        PriceDomain existingPriceById = findById(priceDomain.getId());
        try {
            objectMapper.updateValue(existingPriceById, priceDomain);
        } catch (JsonMappingException e) {
            throw new MapperException(e);
        }
        return this.save(existingPriceById);
    }


    @Override
    public PriceDomain findById(Long id) {
        return serviceRepositoryAdapter.findById(id).orElseThrow(() -> new PriceNotFoundException(id));
    }

    @Override
    public void revertDelete(Long id) {

        PriceDomain existingPriceById = findById(id);
        existingPriceById.setDeleted(false);
        serviceRepositoryAdapter.save(existingPriceById);

    }

    @Override
    public boolean deleteByIdCascade(Long id) {
        try {
            PriceDomain existingPriceById = findById(id);
            existingPriceById.setDeleted(true);
            serviceRepositoryAdapter.save(existingPriceById);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void revertDeleteCascade(Long id) {
        List<PriceDomain> prices = serviceRepositoryAdapter.findByUnitId(id);
        if(!prices.isEmpty()){
            prices.forEach(price->revertDelete(price.getId()));
        }
    }



    @Override
    public boolean deletePricesUnderUnit(Long id) {
        List<PriceDomain> prices = serviceRepositoryAdapter.findByUnitId(id);
        if (prices.isEmpty()){
            return true;
        }
        return prices.stream().allMatch(price->deleteByIdCascade(price.getId()));
    }
}
