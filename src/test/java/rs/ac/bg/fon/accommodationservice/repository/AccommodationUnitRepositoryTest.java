package rs.ac.bg.fon.accommodationservice.repository;

import rs.ac.bg.fon.accommodationservice.eventListener.eventHandlers.MessageQueueHandler;
import rs.ac.bg.fon.accommodationservice.model.Accommodation;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.AccommodationUnit;
import rs.ac.bg.fon.accommodationservice.util.DataJpaTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@Tag("datajpa")
class AccommodationUnitRepositoryTest extends DataJpaTestBase {

    @MockBean
    MessageQueueHandler handler;
    @Autowired
    AccommodationRepository accRepository;

    @Autowired
    AccommodationUnitRepository accUnitRepository;

    @Test
    void saveAndFindById() {
        Accommodation acc = Accommodation.builder()
                .name("Apartment 1")
                .accommodationType(AccommodationType.APARTMENT)
                .hostId(5L)
                .build();

        AccommodationUnit accUnit = AccommodationUnit.builder()
                .accommodation(acc)
                .capacity(5)
                .name("Family room")
                .description("Nice quiet room")
                .build();

        accRepository.save(acc);
        AccommodationUnit accUnitSaved = accUnitRepository.save(accUnit);
        Optional<AccommodationUnit> actual = accUnitRepository.findById(accUnitSaved.getId());

        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(accUnitSaved);

    }

    @Test
    void findAllByHostId() {
        Long hostId = 1L;
        Accommodation acc = Accommodation.builder()
                .name("Hotel")
                .accommodationType(AccommodationType.HOTEL)
                .hostId(hostId)
                .deleted(false)
                .build();

        AccommodationUnit room1 = AccommodationUnit.builder()
                .accommodation(acc)
                .capacity(5)
                .name("Family room 1")
                .description("Nice quiet room")
                .deleted(false)
                .build();
        AccommodationUnit room2 = AccommodationUnit.builder()
                .accommodation(acc)
                .capacity(5)
                .name("Family room 2")
                .description("Nice quiet room")
                .deleted(false)
                .build();

        accRepository.save(acc);
        accUnitRepository.save(room1);
        accUnitRepository.save(room2);

        Page<AccommodationUnit> rooms = accUnitRepository.findByAccommodationHostId(hostId, Pageable.ofSize(10));

        assertThat(rooms).isNotEmpty();
        assertThat(rooms.getContent().size()).isEqualTo(2);
        assertThat(rooms.getContent()).contains(room1, room2);
    }
}
