package ro.mihai.careerhub.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(CreateUserRequest request) {

        User user = new User(
                request.getFirstname(),
                request.getLastname(),
                request.getEmail(),
                passwordEncoder.encode(
                        request.getPassword()
                ),
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

    public UserResponse getUserById(
            Long id,
            String currentEmail,
            boolean isAdmin) {

        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id)
                );

        checkAccess(
                user,
                currentEmail,
                isAdmin
        );

        return userMapper.toResponse(user);
    }

    public UserResponse updateUser(
            Long id,
            UpdateUserRequest request,
            String currentEmail,
            boolean isAdmin) {

        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id)
                );

        checkAccess(
                user,
                currentEmail,
                isAdmin
        );

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPhonenumber(request.getPhonenumber());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        User updatedUser =
                userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    public void deleteUser(
            Long id,
            String currentEmail,
            boolean isAdmin) {

        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id)
                );

        checkAccess(
                user,
                currentEmail,
                isAdmin
        );

        userRepository.delete(user);
    }

    private void checkAccess(
            User user,
            String currentEmail,
            boolean isAdmin) {

        if (isAdmin) {
            return;
        }

        if (!user.getEmail().equals(currentEmail)) {
            throw new AccessDeniedException(
                    "You do not have permission to access this user"
            );
        }
    }
}