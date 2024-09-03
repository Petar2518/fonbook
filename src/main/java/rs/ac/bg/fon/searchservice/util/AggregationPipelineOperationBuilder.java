package rs.ac.bg.fon.searchservice.util;

import org.springframework.data.mongodb.core.aggregation.Aggregation;

public class AggregationPipelineOperationBuilder<T> {

    AggregationPipelineBuilder pipelineBuilder;
    private T value;

    public AggregationPipelineOperationBuilder(AggregationPipelineBuilder pipelineBuilder, T value) {
        this.pipelineBuilder = pipelineBuilder;
        this.value = value;
    }

    public AggregationPipelineBuilder into(String fieldName) {
        return addField(fieldName, value);
    }

    private AggregationPipelineBuilder addField(String fieldName, T value) {

        pipelineBuilder.aggregationOperations.add(Aggregation.addFields()
                .addField(fieldName)
                .withValue(value)
                .build());

        return pipelineBuilder;
    }
}
