package rs.ac.bg.fon.searchservice.util;

import rs.ac.bg.fon.searchservice.dto.CriteriaQuery;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;

public class AccommodationQueryBuilder {
    List<AggregationOperation> aggregationOperations = new ArrayList<>();

    public AccommodationQueryBuilder() {
    }

    public CriteriaQuery build() {
        return new CriteriaQuery(aggregationOperations);
    }

    public AccommodationQueryBuilder unwind(String fieldName) {

        aggregationOperations.add(Aggregation.unwind(fieldName));
        return this;
    }

    public AccommodationQueryBuilder notEmptyContainsAll(String fieldName, List<String> values) {

        if (values == null || values.isEmpty())
            return this;

        values.forEach(value -> {
            notNullEqualCriteria(fieldName, value);
        });

        return this;
    }

    public AccommodationQueryBuilder joinByAggregationPipeline(String collectionName, AggregationPipeline aggregationPipeline) {

        if (aggregationPipeline.equals(new AggregationPipeline()))
            return this;

        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from(collectionName)
                .localField(AccommodationCriteria.LOCAL_UNIT_FIELD)
                .foreignField(AccommodationCriteria.JOINED_COLLECTION_UNIT_FIELD)
                .pipeline(aggregationPipeline)
                .as(buildJoinFieldFromCollectionName(collectionName));

        aggregationOperations.add(lookupOperation);
        return this;
    }

    private String buildJoinFieldFromCollectionName(String collectionName) {
        return AccommodationCriteria.UNITS + "." + collectionName + "s";
    }

    public AccommodationQueryBuilder isEmptyArray(String fieldName) {
        Criteria lookupMatch = Criteria.where(fieldName).is(List.of());
        aggregationOperations.add(new MatchOperation(lookupMatch));
        return this;
    }


    public AccommodationQueryBuilder notNullContainsCriteria(String fieldName, String value) {

        if (value == null || value.isEmpty())
            return this;

        Criteria criteria = Criteria.where(fieldName).regex(".*" + value + ".*", "i");
        aggregationOperations.add(new MatchOperation(criteria));
        return this;
    }

    public <T> AccommodationQueryBuilder notNullEqualCriteria(String fieldName, T value) {

        if (value == null || value.toString().isEmpty())
            return this;

        Criteria criteria = Criteria.where(fieldName).is(value);
        aggregationOperations.add(new MatchOperation(criteria));
        return this;
    }

    public <T> AccommodationQueryBuilder notNullGreaterThanEquals(String key, T value) {

        if (value == null)
            return this;

        Criteria criteria = Criteria.where(key).gte(value);

        aggregationOperations.add(new MatchOperation(criteria));
        return this;
    }

    public <T> AccommodationQueryBuilder notNullLessThanEquals(String key, T value) {

        if (value == null)
            return this;

        Criteria criteria = Criteria.where(key).lte(value);

        aggregationOperations.add(new MatchOperation(criteria));
        return this;
    }


}