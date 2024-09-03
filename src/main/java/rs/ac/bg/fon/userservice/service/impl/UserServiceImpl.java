package rs.ac.bg.fon.userservice.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rs.ac.bg.fon.userservice.dto.UserDto;
import rs.ac.bg.fon.userservice.exception.ActivateUserException;
import rs.ac.bg.fon.userservice.exception.UserExistsException;
import rs.ac.bg.fon.userservice.exception.UserNotFoundException;
import rs.ac.bg.fon.userservice.mapper.UserMapper;
import rs.ac.bg.fon.userservice.repository.UserRepository;
import rs.ac.bg.fon.userservice.service.UserService;
import rs.ac.bg.fon.userservice.service.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = mapper.fromDtoToModel(userDto);
        repository.findById(user.getUserId()).ifPresent(e -> {
            throw new UserExistsException("User with id " + user.getUserId() + " already exist.");
        });
        user.setActive(false);
        return mapper.fromModelToDto(repository.save(user));
    }

    @Override
    public UserDto getUser(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id " + id + " does not exist."));

        return mapper.fromModelToDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = mapper.fromDtoToModel(userDto);

        repository.findById(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with id " + user.getUserId() + " does not exist."));

        return mapper.fromModelToDto(repository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void activateById(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id " + id + " does not exist."));
        try {
            user.setActive(true);
           repository.save(user);
        }catch (Exception e){
            throw new ActivateUserException("User with id" + id + " cannot be activated!");
        }

    }

}
