package rs.ac.bg.fon.reservationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ProfileInfo {
    private Role role;
    private Long id;
    private String sub;
}
