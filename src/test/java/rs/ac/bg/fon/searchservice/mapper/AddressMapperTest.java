package rs.ac.bg.fon.searchservice.mapper;

import rs.ac.bg.fon.searchservice.domain.AddressDomain;
import rs.ac.bg.fon.searchservice.dto.message.AddressMessageDto;
import rs.ac.bg.fon.searchservice.model.Address;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("springboot")
public class AddressMapperTest {

    @Autowired
    AddressMapper addressMapper;

    @Test
    void domainToEntity() {
        AddressDomain addressDomain = AddressDomain.builder()
                .id(1L)
                .city("Krusevac")
                .country("Srbija")
                .postalCode("37000").build();

        Address expected = Address.builder()
                .id(1L)
                .city("Krusevac")
                .country("Srbija")
                .postalCode("37000").build();

        Address address = addressMapper.domainToEntity(addressDomain);

        assertThat(address).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void messageDtoToDomain() {
        AddressMessageDto addressMessageDto = AddressMessageDto.builder()
                .id(1L)
                .city("Krusevac")
                .country("Srbija")
                .postalCode("37000")
                .latitude("lat")
                .longitude("long")
                .build();

        AddressDomain expected = AddressDomain.builder()
                .id(1L)
                .city("Krusevac")
                .country("Srbija")
                .postalCode("37000").build();

        AddressDomain addressDomain = addressMapper.messageDtoToDomain(addressMessageDto);

        assertThat(addressDomain).usingRecursiveComparison().isEqualTo(expected);
    }
}
