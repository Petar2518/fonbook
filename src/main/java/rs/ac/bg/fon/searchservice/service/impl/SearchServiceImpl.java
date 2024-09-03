package rs.ac.bg.fon.searchservice.service.impl;

import rs.ac.bg.fon.searchservice.adapters.serviceRepository.AccommodationDomainEntityAdapter;
import rs.ac.bg.fon.searchservice.domain.AccommodationDomain;
import rs.ac.bg.fon.searchservice.dto.CriteriaQuery;
import rs.ac.bg.fon.searchservice.dto.SearchRequest;
import rs.ac.bg.fon.searchservice.model.ReservationStatus;
import rs.ac.bg.fon.searchservice.service.SearchService;
import rs.ac.bg.fon.searchservice.util.AccommodationCriteria;
import rs.ac.bg.fon.searchservice.util.AccommodationQueryBuilder;
import rs.ac.bg.fon.searchservice.util.AggregationPipelineBuilder;
import rs.ac.bg.fon.searchservice.util.MeanOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final AccommodationDomainEntityAdapter accommodationAdapter;

    public Page<AccommodationDomain> findByQuery(SearchRequest searchRequest, Pageable pageable) {

        CriteriaQuery criteriaQuery = new AccommodationQueryBuilder()
                .notNullEqualCriteria(AccommodationCriteria.TYPE, searchRequest.getType())
                .notNullContainsCriteria(AccommodationCriteria.NAME, searchRequest.getName())
                .notNullContainsCriteria(AccommodationCriteria.CITY, searchRequest.getCity())
                .notNullContainsCriteria(AccommodationCriteria.COUNTRY, searchRequest.getCountry())
                .notEmptyContainsAll(AccommodationCriteria.AMENITY, searchRequest.getAmenities())
                .unwind(AccommodationCriteria.UNITS)
                .notNullEqualCriteria(AccommodationCriteria.CAPACITY, searchRequest.getCapacity())
                .joinByAggregationPipeline(AccommodationCriteria.RESERVATION_COLLECTION, reservationsAggregationPipeline(searchRequest.getCheckIn(), searchRequest.getCheckOut()))
                .isEmptyArray(AccommodationCriteria.RESERVATIONS)
                .joinByAggregationPipeline(AccommodationCriteria.PRICE_COLLECTION, pricesAggregationPipeline(searchRequest.getCheckIn(), searchRequest.getCheckOut()))
                .notNullGreaterThanEquals(AccommodationCriteria.UNIT_MEAN_PRICE, searchRequest.getMinPrice())
                .notNullLessThanEquals(AccommodationCriteria.UNIT_MEAN_PRICE, searchRequest.getMaxPrice())
                .build();

        return accommodationAdapter.queryByRequiredCriteria(criteriaQuery, pageable);
    }

    public AggregationPipeline reservationsAggregationPipeline(LocalDate checkInDate, LocalDate checkOutDate) {

        if (checkInDate == null && checkOutDate == null)
            return new AggregationPipeline();

        return new AggregationPipelineBuilder()
                .searchRequestDates(checkInDate, checkOutDate)
                .notNullEqualCriteria(AccommodationCriteria.RESERVATION_STATUS, ReservationStatus.ACTIVE)
                .datesMatchOperation(AccommodationCriteria.CHECK_IN_DATE, AccommodationCriteria.CHECK_OUT_DATE)
                .build();
    }

    public AggregationPipeline pricesAggregationPipeline(LocalDate checkInDate, LocalDate checkOutDate) {

        if (checkInDate == null || checkOutDate == null)
            return new AggregationPipeline();

        DateOperators.DateDiff dateDiff = dateDifference(
                min(AccommodationCriteria.DATE_TO, checkOutDate),
                max(AccommodationCriteria.DATE_FROM, checkInDate));

        return new AggregationPipelineBuilder()
                .searchRequestDates(checkInDate, checkOutDate)
                .datesMatchOperation(AccommodationCriteria.DATE_FROM, AccommodationCriteria.DATE_TO)
                .multiply(dateDiff, AccommodationCriteria.AMOUNT)
                .into(AccommodationCriteria.PRICE_FOR_RANGE)
                .mean(MeanOperation.builder()
                        .of(AccommodationCriteria.PRICE_FOR_RANGE)
                        .byDays(ChronoUnit.DAYS.between(checkInDate, checkOutDate))
                        .insertSumInto(AccommodationCriteria.TOTAL_AMOUNT)
                        .build())
                .into(AccommodationCriteria.MEAN_PRICE)
                .build();
    }

    private DateOperators.DateDiff dateDifference(AggregationExpression firstDate, AggregationExpression secondDate) {
        return DateOperators.DateDiff
                .diffValueOf(firstDate, AccommodationCriteria.DAY)
                .toDateOf(secondDate);
    }

    private AggregationExpression min(String fieldReference, LocalDate localDate) {
        return ConditionalOperators
                .when(ComparisonOperators.valueOf(fieldReference).lessThanValue(localDate))
                .thenValueOf(fieldReference)
                .otherwise(localDate);
    }

    private AggregationExpression max(String fieldReference, LocalDate localDate) {
        return ConditionalOperators
                .when(ComparisonOperators.valueOf(fieldReference).greaterThanValue(localDate))
                .thenValueOf(fieldReference)
                .otherwise(localDate);
    }
}
