package rs.ac.bg.fon.searchservice.util;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MeanOperation {

    private String of;
    private long byDays;
    private String insertSumInto;

}
