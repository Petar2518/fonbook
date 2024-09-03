package rs.ac.bg.fon.reservationservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import rs.ac.bg.fon.reservationservice.adapters.ReservationDomainEntityAdapter;
import rs.ac.bg.fon.reservationservice.domain.ReservationDomain;
import rs.ac.bg.fon.reservationservice.domain.SearchReservationDomain;
import rs.ac.bg.fon.reservationservice.exceptions.*;
import rs.ac.bg.fon.reservationservice.feignclient.AccommodationClient;
import rs.ac.bg.fon.reservationservice.feignclient.Price;
import rs.ac.bg.fon.reservationservice.model.ReservationStatus;
import rs.ac.bg.fon.reservationservice.model.Role;
import rs.ac.bg.fon.reservationservice.service.ReservationService;
import rs.ac.bg.fon.reservationservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {


    private final ReservationDomainEntityAdapter domainEntityAdapter;
    private final AccommodationClient accommodationClient;

    private final JwtUtil jwtUtil;


    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Retryable
    @Override
    public Long save(ReservationDomain reservationDomain) {

        reservationDomain.setStatus(ReservationStatus.ACTIVE);
        reservationDomain.setCreationDate(LocalDate.now());

        validateThereIsNoConflictingReservations(reservationDomain);

        LocalDate checkIn = reservationDomain.getDateRange().getCheckInDate();
        LocalDate checkOut = reservationDomain.getDateRange().getCheckOutDate();

        List<Price> prices = accommodationClient.getAllPrices(reservationDomain.getAccommodationUnitId(), checkIn, checkOut);

        BigDecimal totalAmount = calculateTotalAmount(prices, checkIn, checkOut);

        reservationDomain.setCurrency(prices.get(0).getCurrency());
        reservationDomain.setTotalAmount(totalAmount);
        return domainEntityAdapter
                .save(reservationDomain);
    }

    public void validateThereIsNoConflictingReservations(ReservationDomain reservationDomain) {
        if (!findConflictingReservations(reservationDomain).isEmpty()) {
            throw new ReservationAlreadyExistsException(reservationDomain.getDateRange().getCheckInDate(), reservationDomain.getDateRange().getCheckOutDate());

        }
    }

    public List<ReservationDomain> findConflictingReservations(ReservationDomain reservationDomain) {
        return domainEntityAdapter
                .getAllExistingReservationsBetweenDates(
                        reservationDomain.getDateRange().getCheckInDate(),
                        reservationDomain.getDateRange().getCheckOutDate(),
                        reservationDomain.getAccommodationUnitId()
                ).stream().filter(reservation -> !reservation.getId().equals(reservationDomain.getId()))
                .toList();

    }

    private BigDecimal calculateTotalAmount(List<Price> prices, LocalDate checkIn, LocalDate checkOut) {
        BigDecimal totalAmount = new BigDecimal(0);

        validatePricingCoverageForReservation(prices, checkIn, checkOut);

           for(Price price: prices){

               LocalDate currentEndDate = (!checkOut.isAfter(price.getDateTo())) ? checkOut : price.getDateTo();
               LocalDate currentStartDate = (!checkIn.isBefore(price.getDateFrom())) ? checkIn : price.getDateFrom();

               int  nights = (int)ChronoUnit.DAYS.between(currentStartDate, currentEndDate);
               totalAmount = totalAmount.add(price.getAmount().multiply(BigDecimal.valueOf(nights)));

           }


       return totalAmount;


    }

    public static void validatePricingCoverageForReservation(List<Price> prices, LocalDate checkIn, LocalDate checkOut){
        if (prices.isEmpty()) {
            throw new NotDefinedPricesForDatesException();
        }

        Price  minPriceDate  = prices.stream().min(Comparator.comparing(Price::getDateFrom)).get();
        Price  maxPriceDate = prices.stream().max(Comparator.comparing(Price::getDateTo)).get();

        if(((minPriceDate.getDateFrom().isAfter(checkIn)) || maxPriceDate.getDateTo().isBefore(checkOut))) {
            throw new NotDefinedPricesForDatesException();
        }

    }

    @Override
    public ReservationDomain getById(Long id) {
        return domainEntityAdapter
                .getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
    }

    @Override
    public void delete(Long id) {
        domainEntityAdapter
                .delete(id);
    }

    @Override
    public Page<ReservationDomain> getAllByProfileId(SearchReservationDomain searchReservationDomain, String jwt) {

        LocalDate minCheckIn = LocalDate.of(1900, 1, 1);

        LocalDate maxCheckOut = LocalDate.of(6000, 1, 1);

        LocalDate newCheckIn = searchReservationDomain.getCheckInDate();
        LocalDate newCheckOut = searchReservationDomain.getCheckOutDate();


        if (newCheckIn != null) {
            minCheckIn = newCheckIn;
        }
        if (newCheckOut != null) {
            maxCheckOut = newCheckOut;
        }

        Role role = jwtUtil.getFromJwt(jwt).getRole();
        Long id = jwtUtil.getFromJwt(jwt).getId();

        switch (role) {
            case USER -> {
                return domainEntityAdapter.getAllByProfileIdBetween(minCheckIn, maxCheckOut, id, searchReservationDomain.getPageable());
            }
            case HOST -> {

                List<Long> ids = new ArrayList<>();
                JsonNode accommodationUnitsDto = accommodationClient.getAccommodationUnitsByHostId(jwt);
                for (JsonNode accUnit : accommodationUnitsDto)
                    ids.add(accUnit.get("id").asLong());

                return domainEntityAdapter.getAllByUnitsIdBetween(minCheckIn, maxCheckOut, ids, searchReservationDomain.getPageable());
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public Page<ReservationDomain> getAll(Pageable pageable) {
        return domainEntityAdapter.getAll(pageable);
    }

    /*
        @Transactional is not used in this case because the likelihood of
        multiple concurrent requests attempting to cancel the same reservation simultaneously
        is relatively low and cannot do any harm.
     */
    @Override
    public void cancelReservation(Long reservationId, Long userId) {
        ReservationDomain reservationDomain = getById(reservationId);

        if (!reservationDomain.getProfileId().equals(userId)) {
            throw new CancelReservationUnauthorizedException();
        }
        if (reservationDomain.getStatus().equals(ReservationStatus.ACTIVE)) {
            reservationDomain.setStatus(ReservationStatus.CANCELED);
            domainEntityAdapter.update(reservationDomain);
        } else {
            throw new ReservationNotLongerExistsException();
        }
    }

    @Override
    public void processPayment(Long reservationId) {
        ReservationDomain reservation = getById(reservationId);

        if (reservation.isPaid()) {
            log.info("Reservation with id: {} is already paid", reservation.getId());
            throw new ReservationAlreadyPaidException();
        }
        reservation.setPaid(true);

        domainEntityAdapter.save(reservation);
    }

}

