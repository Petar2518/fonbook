package rs.ac.bg.fon.hostservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HostDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private String bankAccountNumber;
}
