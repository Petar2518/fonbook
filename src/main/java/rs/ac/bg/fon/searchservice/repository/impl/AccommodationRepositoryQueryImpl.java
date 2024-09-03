package rs.ac.bg.fon.searchservice.repository.impl;

import rs.ac.bg.fon.searchservice.dto.CriteriaQuery;
import rs.ac.bg.fon.searchservice.model.Accommodation;
import rs.ac.bg.fon.searchservice.repository.AccommodationRepositoryQuery;
import rs.ac.bg.fon.searchservice.util.AccommodationCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


@RequiredArgsConstructor
@Repository
public class AccommodationRepositoryQueryImpl implements AccommodationRepositoryQuery {

    private final MongoTemplate mongoTemplate;

    public GroupOperation groupOperation() {

        return group(AccommodationCriteria.ID)
                .first(AccommodationCriteria.NAME).as(AccommodationCriteria.NAME)
                .first(AccommodationCriteria.TYPE).as(AccommodationCriteria.TYPE)
                .first(AccommodationCriteria.ADDRESS).as(AccommodationCriteria.ADDRESS)
                .first(AccommodationCriteria.AMENITIES).as(AccommodationCriteria.AMENITIES)
                .addToSet(AccommodationCriteria.UNITS).as(AccommodationCriteria.UNITS);
    }

    @Override
    public Page<Accommodation> findAllByCriteria(CriteriaQuery criteriaQuery, Pageable pageable) {

        List<AggregationOperation> aggregationOperations =
                Stream.concat(
                        criteriaQuery.getAggregateOperations().stream(),
                        Stream.of(
                                groupOperation(),
                                sort(Sort.by(AccommodationCriteria.ID).ascending()),
                                skip((long) pageable.getPageNumber() * pageable.getPageSize()),
                                limit(pageable.getPageSize()))).collect(Collectors.toList());


        Aggregation aggregation = newAggregation(
                aggregationOperations
        );

        List<Accommodation> resultAgr = mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(Accommodation.class), Accommodation.class).getMappedResults();
        return new PageImpl<>(resultAgr, pageable, resultAgr.size());
    }


}
