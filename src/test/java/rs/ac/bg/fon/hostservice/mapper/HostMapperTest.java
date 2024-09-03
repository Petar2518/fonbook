package rs.ac.bg.fon.hostservice.mapper;

import rs.ac.bg.fon.hostservice.dto.HostDto;
import rs.ac.bg.fon.hostservice.model.Host;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Tag("springboot")
@SpringBootTest
class HostMapperTest {

    @Autowired
    private HostMapper hostMapper;


    @Test
    public void  whenCalled_shouldMapHostDtoToHostEntity(){
        HostDto hostDto = createTestHostDto(1L);

        Host host = hostMapper.toEntity(hostDto);

        assertEquals(hostDto.getName(), host.getName());
        assertEquals(hostDto.getId(), host.getId());
        assertEquals(hostDto.getPhoneNumber(), host.getPhoneNumber());
        assertEquals(hostDto.getBankAccountNumber(), host.getBankAccountNumber());
    }

    @Test
    public void whenCalled_shouldMapHostEntityToHostDto(){
        Host host = createTestHost(1L);

        HostDto hostDto = hostMapper.toDto(host);

        assertEquals(hostDto.getName(), host.getName());
        assertEquals(hostDto.getId(), host.getId());
        assertEquals(hostDto.getPhoneNumber(), host.getPhoneNumber());
        assertEquals(hostDto.getBankAccountNumber(), host.getBankAccountNumber());
    }
    private HostDto createTestHostDto(Long id) {
        return HostDto.builder()
                .id(id)
                .name("Person1")
                .phoneNumber("123123123")
                .bankAccountNumber("12345626576")
                .build();
    }
    private Host createTestHost(Long id) {
        return Host.builder()
                .id(id)
                .name("Person1")
                .phoneNumber("123123123")
                .bankAccountNumber("12345626576")
                .build();
    }

}