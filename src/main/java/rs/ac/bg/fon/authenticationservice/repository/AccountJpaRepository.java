package rs.ac.bg.fon.authenticationservice.repository;

import rs.ac.bg.fon.authenticationservice.repository.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
