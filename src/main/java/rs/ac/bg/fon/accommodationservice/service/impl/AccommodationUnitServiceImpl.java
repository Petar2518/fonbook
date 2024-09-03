package rs.ac.bg.fon.accommodationservice.service.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.AccommodationUnitDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AccommodationUnitDomainUpdate;
import rs.ac.bg.fon.accommodationservice.dto.message.IdMessageDto;
import rs.ac.bg.fon.accommodationservice.exception.specific.AccommodationUnitNotFoundException;
import rs.ac.bg.fon.accommodationservice.exception.specific.MapperException;
import rs.ac.bg.fon.accommodationservice.service.AccommodationUnitService;
import rs.ac.bg.fon.accommodationservice.service.PriceService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccommodationUnitServiceImpl implements AccommodationUnitService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.start.delete.exchanges}")
    private String startTransactionExchange;

    @Value("${rabbitmq.start.delete.accommodationUnit.routing-key}")
    private String accommodationUnitStartTransactionKey;

    private final AccommodationUnitDomainEntityAdapter serviceRepositoryAdapter;
    private final PriceService priceService;
    private final ObjectMapper objectMapper;

    @Override
    public Long save(AccommodationUnitDomain accommodationUnitDomain) {
        accommodationUnitDomain.setDeleted(false);
        return serviceRepositoryAdapter.save(accommodationUnitDomain);
    }

    @Override
    public List<AccommodationUnitDomain> getAll(Long accommodationId, Pageable pageable) {
        return serviceRepositoryAdapter.findAll(accommodationId, pageable);
    }

    @Override
    public void deleteById(Long id) {

        try {
            AccommodationUnitDomain existingUnitById = findById(id);
            existingUnitById.setDeleted(true);
            if (priceService.deletePricesUnderUnit(id)) {
                serviceRepositoryAdapter.save(existingUnitById);
                rabbitTemplate.convertAndSend(startTransactionExchange, accommodationUnitStartTransactionKey, new IdMessageDto(id));
            }else{
                revertDelete(id);
            }
        }catch (Exception e){
            revertDelete(id);
        }
    }


    @Override
    public Long update(AccommodationUnitDomainUpdate accommodationUnitDomainUpdate) {
        AccommodationUnitDomain oldAccommodationUnit = findById(accommodationUnitDomainUpdate.getId());
        try {
            objectMapper.updateValue(oldAccommodationUnit, accommodationUnitDomainUpdate);
        } catch (JsonMappingException e) {
            throw new MapperException(e);
        }
        return serviceRepositoryAdapter.save(oldAccommodationUnit);
    }

    @Override
    public AccommodationUnitDomain findById(Long id) {
        return serviceRepositoryAdapter.findById(id).orElseThrow(() -> new AccommodationUnitNotFoundException(id));
    }

    @Override
    public List<AccommodationUnitDomain> getAllByHost(Long hostId, Pageable pageable) {
        return serviceRepositoryAdapter.findAllByHostId(hostId, pageable);
    }

    @Override
    public void revertDelete(Long id) {
        priceService.revertDeleteCascade(id);
        AccommodationUnitDomain existingAccById = findById(id);
        existingAccById.setDeleted(false);
        serviceRepositoryAdapter.save(existingAccById);
    }

    @Override
    public List<AccommodationUnitDomain> getAllByAccommodation(Long accommodationId){
        return serviceRepositoryAdapter.findAllByAccommodationId(accommodationId);
    }

    @Override
    public boolean deleteAllUnitsUnderAccommodation(Long id) {
        List<AccommodationUnitDomain> units = getAllByAccommodation(id);

        if (units.isEmpty()) {
            return true;
        }
        return units.stream().allMatch(unit -> deleteByIdCascade(unit.getId()));

    }
        @Override
        public boolean deleteByIdCascade(Long id) {
            try {
                AccommodationUnitDomain existingUnitById = findById(id);
                existingUnitById.setDeleted(true);
                if (priceService.deletePricesUnderUnit(id)) {
                    serviceRepositoryAdapter.save(existingUnitById);
                    return true;
                }
                return false;

            }
            catch(Exception e){
                return false;
            }

        }

    @Override
    public void revertDeleteCascade(Long id) {
        List<AccommodationUnitDomain> units = getAllByAccommodation(id);
        if (!units.isEmpty()){
            units.forEach(unit->revertDelete(unit.getId()));
        }
    }

}
