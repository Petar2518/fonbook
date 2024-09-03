package rs.ac.bg.fon.searchservice.service.impl;

import rs.ac.bg.fon.searchservice.domain.AccommodationDomain;
import rs.ac.bg.fon.searchservice.domain.AccommodationUnitDomain;
import rs.ac.bg.fon.searchservice.domain.AddressDomain;
import rs.ac.bg.fon.searchservice.mapper.AccommodationMapper;
import rs.ac.bg.fon.searchservice.mapper.AccommodationUnitMapper;
import rs.ac.bg.fon.searchservice.mapper.AddressMapper;
import rs.ac.bg.fon.searchservice.model.Accommodation;
import rs.ac.bg.fon.searchservice.model.AccommodationUnit;
import rs.ac.bg.fon.searchservice.repository.AccommodationRepository;
import rs.ac.bg.fon.searchservice.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationMapper accommodationMapper;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationUnitMapper accommodationUnitMapper;
    private final AddressMapper addressMapper;

    @Override
    public void save(AccommodationDomain accommodationDomain) {
        Accommodation accommodation = accommodationMapper.domainToEntity(accommodationDomain);

        accommodationRepository.save(accommodation);
        log.info("Saved accommodation {}", accommodation);
    }

    @Override
    public void addUnit(AccommodationUnitDomain unit, long id) {
        AccommodationUnit forSave = accommodationUnitMapper.domainToEntity(unit);

        accommodationRepository.findById(id).ifPresent(
                accommodation -> addUnitAndSave(accommodation, forSave)
        );
    }

    private void addUnitAndSave(Accommodation accommodation, AccommodationUnit forSave) {

        accommodation.getAccommodationUnits().removeIf(unit -> unit.getId().equals(forSave.getId()));
        accommodation.getAccommodationUnits().add(forSave);

        accommodationRepository.save(accommodation);
        log.info("Added unit {} to accommodation with id {}", forSave, accommodation.getId());
    }

    @Override
    public void addAddress(AddressDomain addressDomain, long id) {
        accommodationRepository.findById(id).ifPresent(
                accommodation -> {
                    accommodation.setAddress(addressMapper.domainToEntity(addressDomain));
                    accommodationRepository.save(accommodation);
                    log.info("Added address {} to accommodation with id {}", accommodation.getAddress(), accommodation.getId());
                }
        );
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepository.deleteById(id);
        log.info("Deleted accommodation with id {}", id);
    }

    @Override
    public void deleteUnit(Long id) {
        accommodationRepository.findByAccommodationUnitsId(id).ifPresent(
                accommodation -> {
                    accommodation.getAccommodationUnits().removeIf(u -> u.getId().equals(id));
                    accommodationRepository.save(accommodation);
                    log.info("Deleted accommodation unit with id {} from accommodation with id {}", id, accommodation.getId());
                }
        );
    }

    @Override
    public void deleteAddress(Long id) {
        accommodationRepository.findById(id).ifPresent(
                accommodation -> {
                    accommodation.setAddress(null);
                    accommodationRepository.save(accommodation);
                    log.info("Deleted address from accommodation with id {}", accommodation.getId());
                }
        );
    }
}
