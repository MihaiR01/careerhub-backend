package ro.mihai.careerhub.mapper;

import org.springframework.stereotype.Component;
import ro.mihai.careerhub.dto.response.UserResponse;
import ro.mihai.careerhub.entity.User;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getPhonenumber(),
                user.getCreatedate()
        );
    }
}