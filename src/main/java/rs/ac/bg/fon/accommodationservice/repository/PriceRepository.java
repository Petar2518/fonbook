package rs.ac.bg.fon.accommodationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.bg.fon.accommodationservice.domain.PriceDomain;
import rs.ac.bg.fon.accommodationservice.model.Price;

import java.time.LocalDate;
import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {

    @Query(value = "SELECT p FROM Price p WHERE p.accommodationUnit.id = :accommodationUnit "
            + "AND ((p.dateFrom >= :before AND p.dateFrom < :after) or "
            + "(p.dateTo > :before and p.dateTo <= :after) or "
            + "(p.dateFrom < :before and p.dateTo > :after))")
    List<Price> findAllAccommodationUnitsInDates(@Param("accommodationUnit") Long accommodationUnit,
                                                 @Param("before") LocalDate before,
                                                 @Param("after") LocalDate after
    );


    @Query(value = "SELECT p FROM Price p Where p.accommodationUnit.id = :id")
    List<Price> findAllPricesForAccommodationUnit(@Param("id") Long id);
}
