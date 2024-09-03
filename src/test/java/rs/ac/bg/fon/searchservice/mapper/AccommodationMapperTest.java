package rs.ac.bg.fon.searchservice.mapper;

import rs.ac.bg.fon.searchservice.domain.AccommodationDomain;
import rs.ac.bg.fon.searchservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.searchservice.dto.AccommodationDto;
import rs.ac.bg.fon.searchservice.dto.message.AccommodationMessageDto;
import rs.ac.bg.fon.searchservice.model.Accommodation;
import rs.ac.bg.fon.searchservice.model.AccommodationType;
import rs.ac.bg.fon.searchservice.model.AccommodationUnit;
import rs.ac.bg.fon.searchservice.model.Address;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("springboot")
class AccommodationMapperTest {

    @Autowired
    AccommodationMapper accommodationMapper;
    static AccommodationDomain accommodationDomain;
    static Accommodation accommodation;


    @BeforeAll
    static void setUp() {

        Address entityAddress = Address.builder()
                .id(34)
                .country("testCountry")
                .city("testCity")
                .street("testStreet")
                .streetNumber("66")
                .postalCode("5543")
                .build();

        AccommodationUnit accommodationUnit = AccommodationUnit.builder()
                .id(99L)
                .capacity(3)
                .build();

        AccommodationUnitDomain accommodationUnitDomain = AccommodationUnitDomain.builder()
                .id(99L)
                .capacity(3)
                .build();

        List<AccommodationUnit> accommodationUnits =
                Collections.singletonList(accommodationUnit);

        List<AccommodationUnitDomain> accommodationUnitDomains =
                Collections.singletonList(accommodationUnitDomain);

        accommodation = Accommodation.builder()
                .id(87)
                .name("AccommodationName")
                .accommodationType(AccommodationType.HOTEL)
                .address(entityAddress)
                .accommodationUnits(new HashSet<>(accommodationUnits))
                .build();


        accommodationDomain = AccommodationDomain.builder()
                .id(87)
                .name("AccommodationDomainName")
                .accommodationType(AccommodationType.COTTAGE)
                .address(entityAddress)
                .accommodationUnits(new HashSet<>(accommodationUnitDomains))
                .build();
    }


    @Test
    void entityToDomain() {

        AccommodationDomain accommodationDomain = accommodationMapper.entityToDomain(accommodation);

        assertThat(accommodation.getId()).isEqualTo(accommodationDomain.getId());
        assertThat(accommodation.getName()).isEqualTo(accommodationDomain.getName());
        assertThat(accommodation.getAccommodationType()).isEqualTo(accommodationDomain.getAccommodationType());
        assertThat(accommodation.getAddress()).usingRecursiveComparison().isEqualTo(accommodationDomain.getAddress());

    }

    @Test
    void entitiesToDomains() {

        List<Accommodation> accommodations = new ArrayList<>();
        accommodations.add(accommodation);

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Accommodation> accommodationPage = new PageImpl<>(accommodations, pageRequest, accommodations.size());

        Page<AccommodationDomain> accommodationDomains = accommodationMapper.entitiesToDomains(accommodationPage);

        assertThat(accommodationPage.getTotalElements()).isEqualTo(accommodationDomains.getTotalElements());
        assertThat(accommodationPage.getTotalPages()).isEqualTo(accommodationDomains.getTotalPages());

        for (int i = 0; i < accommodations.size(); i++) {

            AccommodationDomain accommodationDomain = accommodationDomains.getContent().get(i);
            assertThat(accommodations.get(i).getId()).isEqualTo(accommodationDomain.getId());
            assertThat(accommodations.get(i).getName()).isEqualTo(accommodationDomain.getName());
        }

    }

    @Test
    void domainToDto() {

        AccommodationDto accommodationDto = accommodationMapper.domainToDto(accommodationDomain);

        assertThat(accommodationDto.getId()).isEqualTo(accommodationDomain.getId());
        assertThat(accommodationDto.getName()).isEqualTo(accommodationDomain.getName());

    }

    @Test
    void messageDtoToDomain() {
        AccommodationMessageDto accommodationMessageDto = AccommodationMessageDto.builder()
                .id(1L)
                .name("acc name")
                .accommodationType(AccommodationType.HOTEL)
                .description("description")
                .hostId(2L)
                .build();
        AccommodationDomain expectedDomain = AccommodationDomain.builder()
                .id(1L)
                .name("acc name")
                .accommodationType(AccommodationType.HOTEL)
                .accommodationUnits(new HashSet<>())
                .build();

        AccommodationDomain accommodationDomain = accommodationMapper.messageDtoToDomain(accommodationMessageDto);

        assertThat(accommodationDomain).usingRecursiveComparison().isEqualTo(expectedDomain);
    }
}