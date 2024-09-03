package rs.ac.bg.fon.hostservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostDomain {
    private Long id;
    private String name;
    private String phoneNumber;
    private String bankAccountNumber;
    private boolean active;
}
