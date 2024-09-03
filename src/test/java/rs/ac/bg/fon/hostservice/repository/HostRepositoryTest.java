package rs.ac.bg.fon.hostservice.repository;


import rs.ac.bg.fon.hostservice.model.Host;
import rs.ac.bg.fon.hostservice.utill.DataJpaTestBase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("datajpa")
@ComponentScan(basePackages = {"rs.ac.bg.fon.hostservice.constraints"})
public class HostRepositoryTest extends DataJpaTestBase {

    @Autowired
    private HostRepository hostRepository;


    public long count = 1;


    @Test
    void save_whenRead_expectIdNotNull() {

        Host host = createTestHost();

        hostRepository.save(host);

        Host retrievedHost = hostRepository.findById(host.getId()).get();

        assertThat(retrievedHost.getId()).isNotNull();

    }

    @Test
    public void save_whenRead_expectCorrectAttributes() {

        Host host = createTestHost();

        hostRepository.save(host);

        Host retrievedHost = hostRepository.findById(host.getId()).get();

        assertThat(retrievedHost.getId()).isNotNull();
        assertThat(retrievedHost.getName()).isEqualTo("Person1");
        assertThat(retrievedHost.getPhoneNumber()).isEqualTo("123123123");
        assertThat(retrievedHost.getBankAccountNumber()).isEqualTo("12345626576");

    }

    @Test
    public void save_whenNotValidBankAccountNumber_shouldThrowException() {

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        Host host = createTestHostWithInvalidBackAccount();

        List<ConstraintViolation<Host>> validate = new ArrayList<>(validator.validate(host));

        assertEquals(1, validate.size());
    }

    @Test
    public void updateHostName_whenRead_expectNameChanged() {
        Host host = createTestHost();

        hostRepository.save(host);

        host.setName("ChangedPerson");
        Host retrievedHost = hostRepository.save(host);

        assertThat(retrievedHost.getName()).isEqualTo("ChangedPerson");

    }

    @Test
    public void delete_whenRead_expectHostDeleted() {

        Host host = createTestHost();

        hostRepository.save(host);
        hostRepository.deleteById(host.getId());

        Optional<Host> deletedHost = hostRepository.findById(host.getId());

        assertThat(deletedHost).isEmpty();

    }

    @Test
    public void getListOfHosts_expectNotEmptyList() {

        Host host1 = createTestHost();
        Host host2 = createTestHost();

        hostRepository.save(host1);
        hostRepository.save(host2);

        List<Host> hosts = hostRepository.findAll();

        assertThat(hosts.size()).isGreaterThan(0);

    }

    private Host createTestHost() {
        return Host.builder()
                .id(count++)
                .name("Person1")
                .phoneNumber("123123123")
                .bankAccountNumber("12345626576")
                .build();
    }

    private Host createTestHostWithInvalidBackAccount() {
        return Host.builder()
                .id(count++)
                .name("Person1")
                .phoneNumber("123123123")
                .bankAccountNumber("1234a")
                .build();
    }


}




