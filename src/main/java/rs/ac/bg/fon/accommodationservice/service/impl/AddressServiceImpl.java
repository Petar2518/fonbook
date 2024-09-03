package rs.ac.bg.fon.accommodationservice.service.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.AddressDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.domain.AddressDomain;
import rs.ac.bg.fon.accommodationservice.domain.PriceDomain;
import rs.ac.bg.fon.accommodationservice.domain.update.AddressDomainUpdate;
import rs.ac.bg.fon.accommodationservice.dto.message.IdMessageDto;
import rs.ac.bg.fon.accommodationservice.exception.specific.AddressNotFoundException;
import rs.ac.bg.fon.accommodationservice.exception.specific.MapperException;
import rs.ac.bg.fon.accommodationservice.service.AddressService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressDomainEntityAdapter serviceRepositoryAdapter;
    private final ObjectMapper objectMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.start.delete.exchanges}")
    private String startTransactionExchange;

    @Value("${rabbitmq.start.delete.address.routing-key}")
    private String addressStartTransactionKey;

    @Override
    public Long save(AddressDomain addressDomain) {
        addressDomain.setDeleted(false);
        return serviceRepositoryAdapter.save(addressDomain);
    }

    @Override
    public List<AddressDomain> getAll(Pageable pageable) {
        return serviceRepositoryAdapter.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        try {
            AddressDomain existingAddressById = findById(id);
            existingAddressById.setDeleted(true);
            serviceRepositoryAdapter.save(existingAddressById);
            rabbitTemplate.convertAndSend(startTransactionExchange, addressStartTransactionKey, new IdMessageDto(id));
        } catch (Exception e) {
            revertDelete(id);
        }
    }

    @Override
    public Long update(AddressDomainUpdate addressDomain) {
        AddressDomain existingAddressById = findById(addressDomain.getId());
        try {
            objectMapper.updateValue(existingAddressById, addressDomain);
        } catch (JsonMappingException e) {
            throw new MapperException(e);
        }
        return serviceRepositoryAdapter.save(existingAddressById);
    }

    @Override
    public AddressDomain findById(Long id) {
        return serviceRepositoryAdapter.findById(id).orElseThrow(() -> new AddressNotFoundException(id));
    }

    @Override
    public void revertDelete(Long id) {
        AddressDomain existingAddressById = findById(id);
        existingAddressById.setDeleted(false);
        serviceRepositoryAdapter.save(existingAddressById);
    }

    @Override
    public boolean deleteByIdCascade(Long id) {
        try {
            AddressDomain existingAddressById = findById(id);

            existingAddressById.setDeleted(true);
            serviceRepositoryAdapter.save(existingAddressById);
            return true;
        }catch (AddressNotFoundException e){
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
