package rs.ac.bg.fon.userservice.service;

import rs.ac.bg.fon.userservice.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public UserDto createUser(UserDto user);

    public UserDto getUser(Long id);

    public UserDto updateUser(UserDto user);

    public void deleteUser(Long id);

    void activateById(Long id);
}
