package rs.ac.bg.fon.searchservice.util;

import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

public class AggregationPipelineBuilder {
    List<AggregationOperation> aggregationOperations = new ArrayList<>();

    LocalDate checkInDate;
    LocalDate checkOutDate;

    public AggregationPipelineBuilder() {
    }

    public AggregationPipelineBuilder searchRequestDates(LocalDate checkInDate, LocalDate checkOutDate) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        return this;
    }

    public AggregationPipeline build() {
        return new AggregationPipeline(aggregationOperations);
    }

    public AggregationPipelineBuilder datesMatchOperation(String startDate, String endDate) {

        Criteria dateCriteria = Criteria.where(startDate).lte(checkOutDate).andOperator(Criteria.where(endDate).gte(checkInDate));
        aggregationOperations.add(new MatchOperation(dateCriteria));

        return this;
    }

    public <T> AggregationPipelineBuilder notNullEqualCriteria(String fieldName, T value) {

        if (value == null || value.toString().isEmpty())
            return this;

        Criteria criteria = Criteria.where(fieldName).is(value);
        aggregationOperations.add(new MatchOperation(criteria));

        return this;
    }

    public AggregationPipelineOperationBuilder<ArithmeticOperators.Multiply> multiply(AggregationExpression multiplicand, String multiplier) {
        return new AggregationPipelineOperationBuilder<>(this, multiplyBy(multiplicand, multiplier));
    }

    public AggregationPipelineOperationBuilder<ArithmeticOperators.Divide> mean(MeanOperation operation) {
        sum(operation.getOf(), operation.getInsertSumInto());
        return divide(operation.getInsertSumInto(), operation.getByDays());
    }

    private void sum(String fieldForSum, String newFieldName) {

        aggregationOperations.add(group(AccommodationCriteria.JOINED_COLLECTION_UNIT_FIELD)
                .sum(fieldForSum).as(fieldForSum)
                .sum(fieldForSum).as(newFieldName));

    }

    public AggregationPipelineOperationBuilder<ArithmeticOperators.Divide> divide(String dividendFieldName, Number divisor) {
        return new AggregationPipelineOperationBuilder<>(this, divideBy(dividendFieldName, divisor));
    }

    private ArithmeticOperators.Multiply multiplyBy(AggregationExpression multiplicand, String multiplier) {
        return ArithmeticOperators.Multiply
                .valueOf(multiplicand)
                .multiplyBy(convertToDouble(multiplier));
    }

    private ArithmeticOperators.Divide divideBy(String fieldReference, Number field) {

        return ArithmeticOperators.Divide
                .valueOf(fieldReference)
                .divideBy(field);
    }

    private ConvertOperators.ToDouble convertToDouble(String fieldReference) {
        return ConvertOperators.valueOf(fieldReference).convertToDouble();
    }
}