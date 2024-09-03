package rs.ac.bg.fon.accommodationservice.repository;

import rs.ac.bg.fon.accommodationservice.model.Accommodation;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.Address;
import rs.ac.bg.fon.accommodationservice.util.DataJpaTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@Tag("datajpa")
class AddressRepositoryTest extends DataJpaTestBase {

    @Autowired
    AccommodationRepository accRepository;

    @Autowired
    AddressRepository addressRepository;

    @Test
    void saveAndFindById() {
        Accommodation acc = Accommodation.builder()
                .name("Apartment 1")
                .accommodationType(AccommodationType.APARTMENT)
                .hostId(5L)
                .build();
        Accommodation accSaved = accRepository.save(acc);
        Address address = Address.builder()
                .accommodation(accSaved)
                .country("Serbia")
                .city("Belgrade")
                .street("Bulevar Kralja Aleksandra")
                .postalCode("11000")
                .streetNumber("123a")
                .build();


        Address addressSaved = addressRepository.save(address);
        Optional<Address> actual = addressRepository.findById(addressSaved.getId());
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(addressSaved);

    }
}
