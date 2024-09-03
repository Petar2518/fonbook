package rs.ac.bg.fon.accommodationservice.model.JwtReceiver;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfo {
    @Enumerated(EnumType.STRING)
    private Role role;
    private Long id;

}
