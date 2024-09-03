package rs.ac.bg.fon.hostservice.service;

import rs.ac.bg.fon.hostservice.adapters.HostDomainEntityAdapter;
import rs.ac.bg.fon.hostservice.domain.HostDomain;
import rs.ac.bg.fon.hostservice.exceptions.ResourceNotFoundException;
import rs.ac.bg.fon.hostservice.service.impl.HostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class HostServiceTest {

    @Mock
    HostDomainEntityAdapter domainEntityAdapter;

    @InjectMocks
    HostServiceImpl hostServiceImpl;

    @Test
    void save_success() {
        HostDomain hostDomain = createTestHostDomain(1L);

        hostServiceImpl.save(hostDomain);

        verify(domainEntityAdapter, times(1)).save(hostDomain);
    }
    @Test
    void getById_success() {
        HostDomain expectedHostDomain = createTestHostDomain(1L);
        when(domainEntityAdapter.getById(expectedHostDomain.getId())).thenReturn(Optional.of(expectedHostDomain));

        HostDomain actualHostDomain = hostServiceImpl.getById(expectedHostDomain.getId());

        assertEquals(expectedHostDomain, actualHostDomain);
    }

    @Test
    void getById_ifNotFound_expectException() {
        Long id = 1L;

        when(domainEntityAdapter.getById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hostServiceImpl.getById(id));
        verify(domainEntityAdapter, times(1)).getById(id);
    }
    @Test
    void getAll_success() {
        Page<HostDomain> expectedPage = Page.empty();
        when(domainEntityAdapter.getAll(any(Pageable.class))).thenReturn(expectedPage);

        Pageable pageable = PageRequest.of(0,1);
        Page<HostDomain> actualPage = hostServiceImpl.getAll(pageable);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    void delete_success() {
        hostServiceImpl.delete(1L);

        verify(domainEntityAdapter, times(1)).delete(1L);
    }
    @Test
    void update_success() {
        Long hostId = 1L;
        HostDomain hostDomainInput = new HostDomain();
        hostDomainInput.setId(hostId);
        hostDomainInput.setName("Updated Name");
        hostDomainInput.setBankAccountNumber("1111111111111");
        hostDomainInput.setPhoneNumber("111111111111111");

        HostDomain existingHostDomain = createTestHostDomain(hostId);

        when(domainEntityAdapter.getById(hostId)).thenReturn(Optional.of(existingHostDomain));

        hostServiceImpl.update(hostDomainInput);

        verify(domainEntityAdapter, times(1)).getById(hostId);


        assertEquals("Updated Name", existingHostDomain.getName());
        assertEquals("1111111111111", existingHostDomain.getBankAccountNumber());
        assertEquals("111111111111111", existingHostDomain.getPhoneNumber());

        verify(domainEntityAdapter, times(1)).update(existingHostDomain);
    }

    private HostDomain createTestHostDomain(Long id) {
        return HostDomain.builder()
                .id(id)
                .name("Person1")
                .phoneNumber("123123123")
                .bankAccountNumber("12345626576")
                .build();
    }

}