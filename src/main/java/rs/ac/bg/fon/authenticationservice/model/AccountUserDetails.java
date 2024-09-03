package rs.ac.bg.fon.authenticationservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
public class AccountUserDetails extends User {

    private LocalDateTime tokenRevokedLastAt;

    public AccountUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, LocalDateTime tokenRevokedLastAt) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.tokenRevokedLastAt = tokenRevokedLastAt;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
