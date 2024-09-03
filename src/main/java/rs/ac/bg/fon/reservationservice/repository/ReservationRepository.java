package rs.ac.bg.fon.reservationservice.repository;

import rs.ac.bg.fon.reservationservice.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r " +
            "WHERE ((r.checkInDate <= :checkIn AND r.checkOutDate > :checkIn) " +
            "OR (r.checkInDate > :checkIn  AND (r.checkInDate < :checkOut AND r.checkOutDate >= :checkOut)) " +
            "OR (r.checkInDate > :checkIn AND r.checkOutDate < :checkOut)" +
            "AND r.status = 'ACTIVE') " +
            "AND r.accommodationUnitId = :accommodationUnitId")
    List<Reservation> findAllExistingReservationsForDates(@Param("checkIn") LocalDate checkIn,
                                                          @Param("checkOut") LocalDate checkOut,
                                                          @Param("accommodationUnitId") Long accommodationUnitId);

    @Query("SELECT r FROM Reservation r " +
            "WHERE (r.checkInDate >= :checkInDate " +
            "AND r.checkOutDate <= :checkOutDate )" +
            "AND r.profileId = :profileId")
    Page<Reservation> findReservationsBetweenDateRangeUsingProfileId(@Param("checkInDate") LocalDate checkInDate,
                                                                     @Param("checkOutDate") LocalDate checkOutDate,
                                                                     @Param("profileId") Long profileId,
                                                                     Pageable pageable);

    @Query("SELECT r FROM Reservation r " +
            "WHERE (r.checkInDate >= :checkInDate " +
            "AND r.checkOutDate <= :checkOutDate )" +
            "AND r.accommodationUnitId in :ids")
    Page<Reservation> findReservationsBetweenDateRangeUsingUnitIds(@Param("checkInDate") LocalDate checkInDate,
                                                                   @Param("checkOutDate") LocalDate checkOutDate,
                                                                   @Param("ids") List<Long> ids,
                                                                   Pageable pageable);

}
