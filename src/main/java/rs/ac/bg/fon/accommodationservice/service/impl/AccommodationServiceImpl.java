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
import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.AccommodationDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AccommodationDomainUpdate;
import rs.ac.bg.fon.accommodationservice.dto.message.IdMessageDto;
import rs.ac.bg.fon.accommodationservice.exception.specific.AccommodationNotFoundException;
import rs.ac.bg.fon.accommodationservice.exception.specific.MapperException;
import rs.ac.bg.fon.accommodationservice.exception.specific.UserNotOwnerOfAccommodationException;
import rs.ac.bg.fon.accommodationservice.exception.specific.WrongAccessRoleException;
import rs.ac.bg.fon.accommodationservice.model.JwtReceiver.Role;
import rs.ac.bg.fon.accommodationservice.model.JwtReceiver.UserInfo;
import rs.ac.bg.fon.accommodationservice.service.AccommodationService;
import rs.ac.bg.fon.accommodationservice.service.AccommodationUnitService;
import rs.ac.bg.fon.accommodationservice.service.AddressService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationDomainEntityAdapter serviceRepositoryAdapter;

    private final ObjectMapper objectMapper;

    private final AccommodationUnitService accommodationUnitService;
    private final AddressService addressService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.start.delete.exchanges}")
    private String accommodationStartTransactionExchange;

    @Value("${rabbitmq.start.delete.accommodation.routing-key}")
    private String accommodationStartTransactionKey;


    @Override
    public Long save(AccommodationDomain accommodationDomain, UserInfo user) {
        if (!Objects.equals(user.getRole(), Role.HOST)) {
            throw new WrongAccessRoleException(user.getRole().toString());
        }
        accommodationDomain.setDeleted(false);
        return serviceRepositoryAdapter.save(accommodationDomain);


    }

    @Override
    public Long saveTry(AccommodationDomain accommodationDomain) {
        accommodationDomain.setDeleted(false);
        return serviceRepositoryAdapter.save(accommodationDomain);
    }


    @Override
    public List<AccommodationDomain> getAll(Pageable pageable) {
        return serviceRepositoryAdapter.findAll(pageable);
    }


    @Override
    public void deleteById(Long id, UserInfo userInfo) {
        if (!Objects.equals(userInfo.getRole(), Role.HOST)) {
            throw new WrongAccessRoleException(userInfo.getRole().toString());
        }
        if (Objects.equals(this.findById(id).getHostId(), userInfo.getId())) {
            try {
                AccommodationDomain existingAccById = findById(id);
                existingAccById.setDeleted(true);
                if (deleteAllUnitsAndAddressForAccommodation(id)) {
                    serviceRepositoryAdapter.save(existingAccById);
                    rabbitTemplate.convertAndSend(accommodationStartTransactionExchange, accommodationStartTransactionKey, new IdMessageDto(id));
                }
            }catch(Exception e){
                revertAllCascade(id);
            }
        } else {
            throw new UserNotOwnerOfAccommodationException(userInfo.getId(), id);
        }
    }
    @Override
    public void revertAllCascade(Long id) {
        accommodationUnitService.revertDeleteCascade(id);
        addressService.revertDelete(id);
        revertDelete(id);
    }


    @Override
    public Long update(AccommodationDomainUpdate accommodationDomainUpdate, UserInfo user) {
        AccommodationDomain existingAccById = findById(accommodationDomainUpdate.getId());
        if (!Objects.equals(user.getRole(), Role.HOST)) {
            throw new WrongAccessRoleException(user.getRole().toString());
        }
        if (!Objects.equals(existingAccById.getHostId(), user.getId())) {
            throw new UserNotOwnerOfAccommodationException(user.getId(), existingAccById.getId());
        }
        try {
            objectMapper.updateValue(existingAccById, accommodationDomainUpdate);
        } catch (JsonMappingException e) {
            throw new MapperException(e);
        }
        existingAccById.setDeleted(false);

        return serviceRepositoryAdapter.save(existingAccById);

    }

    @Override
    public AccommodationDomain findById(Long id) {
        return serviceRepositoryAdapter.findById(id).orElseThrow(() -> new AccommodationNotFoundException(id));
    }

    @Override
    public List<AccommodationDomain> getAllByHost(Long hostId, Pageable pageable) {
        return serviceRepositoryAdapter.findAllByHostId(hostId, pageable);
    }

    @Override
    public void deleteById(Long id) {
        try {


            if (deleteAllUnitsAndAddressForAccommodation(id)) {
                AccommodationDomain existingAccById = findById(id);
                existingAccById.setDeleted(true);
                serviceRepositoryAdapter.save(existingAccById);
                rabbitTemplate.convertAndSend(accommodationStartTransactionExchange, accommodationStartTransactionKey, new IdMessageDto(id));
            }else{
                revertAllCascade(id);
            }
        }catch(Exception e){
            revertAllCascade(id);
        }
    }

    @Override
    public boolean deleteAllUnitsAndAddressForAccommodation(Long id) {
        return accommodationUnitService.deleteAllUnitsUnderAccommodation(id) && addressService.deleteByIdCascade(id);
    }

    @Override
    public void revertDelete(Long id){
        AccommodationDomain existingAccById = findById(id);
        existingAccById.setDeleted(false);
        serviceRepositoryAdapter.save(existingAccById);
    }
}
