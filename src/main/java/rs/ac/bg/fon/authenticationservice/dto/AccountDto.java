package rs.ac.bg.fon.authenticationservice.dto;

import rs.ac.bg.fon.authenticationservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDto {
    private Long id;
    private String email;
    private boolean valid;
    private Role role;
}
