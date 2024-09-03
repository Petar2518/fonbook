package rs.ac.bg.fon.searchservice.dto;

import lombok.*;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CriteriaQuery {

    List<AggregationOperation> aggregateOperations;

}


