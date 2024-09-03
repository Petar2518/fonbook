package rs.ac.bg.fon.authenticationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Account {

    private Long accountId;

    private String email;

    private String password;

    private boolean valid;

    private Role role;

    private LocalDateTime tokenRevokedLastAt;

    private boolean active;

}
