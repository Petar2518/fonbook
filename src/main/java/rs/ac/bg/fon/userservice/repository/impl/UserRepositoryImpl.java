package rs.ac.bg.fon.userservice.repository.impl;

import rs.ac.bg.fon.userservice.mapper.UserMapper;
import rs.ac.bg.fon.userservice.repository.UserJpaRepository;
import rs.ac.bg.fon.userservice.service.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements rs.ac.bg.fon.userservice.repository.UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        return mapper.fromEntityToModel(userJpaRepository.save(mapper.fromModelToEntity(user)));
    }

    @Override
    public void deleteById(Long userId) {
        userJpaRepository.deleteById(userId);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(mapper.fromEntityToModel(userJpaRepository.findById(userId).orElse(null)));
    }
}
