package rs.ac.bg.fon.authenticationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PasswordResetToken {

    private UUID id;
    private Account account;
    private LocalDateTime expirationDateTime;
}
