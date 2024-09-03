package rs.ac.bg.fon.reservationservice.repository;


import rs.ac.bg.fon.reservationservice.model.RequestStatus;
import rs.ac.bg.fon.reservationservice.model.Reservation;
import rs.ac.bg.fon.reservationservice.model.ReservationDateSetting;
import rs.ac.bg.fon.reservationservice.model.ReservationStatus;
import rs.ac.bg.fon.reservationservice.util.DataJpaTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@Tag("datajpa")
public class ReservationDateSettingRepositoryTest extends DataJpaTestBase {

    @Autowired
    private ReservationDateSettingRepository reservationDateSettingRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void save_expectIdNotNull() {
        ReservationDateSetting reservationDateSetting = saveReservationDateSetting();

        ReservationDateSetting retrievedReservationDateSetting = reservationDateSettingRepository.findById(reservationDateSetting.getId()).get();

        assertEquals(reservationDateSetting, retrievedReservationDateSetting);
    }

    public ReservationDateSetting saveReservationDateSetting() {
        Reservation reservation = newReservation();
        Reservation savedReservation = reservationRepository.save(reservation);


        ReservationDateSetting reservationDateSetting = newReservationDateSetting(savedReservation);

        reservationDateSettingRepository.save(reservationDateSetting);

        return reservationDateSetting;
    }

    @Test
    public void getById_expectSuccess() {
        Reservation reservation = newReservation();
        Reservation savedReservation = reservationRepository.save(reservation);

        ReservationDateSetting reservationDateSetting = newReservationDateSetting(savedReservation);
        reservationDateSettingRepository.save(reservationDateSetting);

        Optional<ReservationDateSetting> retrievedReservationDateSetting = reservationDateSettingRepository.findById(reservation.getId());

        assertThat(Optional.of(retrievedReservationDateSetting)).isNotEmpty();
        retrievedReservationDateSetting.ifPresent(request -> assertEquals(reservationDateSetting, request));
    }

    ReservationDateSetting newReservationDateSetting(Reservation reservation) {
        return ReservationDateSetting
                .builder()
                .id(reservation.getId())
                .reservation(reservation)
                .updatedCheckIn(LocalDate.now())
                .updatedCheckOut(LocalDate.now().plusDays(5))
                .status(RequestStatus.PENDING)
                .build();
    }

    public Reservation newReservation() {
        double price = 10.00;
        return Reservation.builder()
                .creationDate(LocalDate.now())
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(5))
                .totalAmount(BigDecimal.valueOf(price))
                .status(ReservationStatus.ACTIVE)
                .numberOfPeople(3)
                .accommodationUnitId(1L)
                .profileId(1L)
                .build();
    }


}
