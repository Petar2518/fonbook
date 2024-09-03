package rs.ac.bg.fon.userservice.repository;

import rs.ac.bg.fon.userservice.service.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    public User save(User user);

    public void deleteById(Long userId);

    public Optional<User> findById(Long userId);
}
