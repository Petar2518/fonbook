package rs.ac.bg.fon.bookinggateway.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HostDetailsDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private String bankAccountNumber;
}
