package ro.mihai.careerhub.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ro.mihai.careerhub.dto.request.CreateUserRequest;
import ro.mihai.careerhub.dto.request.UpdateUserRequest;
import ro.mihai.careerhub.dto.response.UserResponse;
import ro.mihai.careerhub.entity.User;
import ro.mihai.careerhub.exception.UserNotFoundException;
import ro.mihai.careerhub.mapper.UserMapper;
import ro.mihai.careerhub.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse createUser(CreateUserRequest request) {

        User user = new User(
                request.getFirstname(),
                request.getLastname(),
                request.getEmail(),
                request.getPassword(),
                request.getPhonenumber()
        );

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toResponse(user);
    }

    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);
    }

    public UserResponse updateUser(
            Long id,
            UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhonenumber(request.getPhonenumber());

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }
}