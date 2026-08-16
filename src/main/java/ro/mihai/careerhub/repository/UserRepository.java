package ro.mihai.careerhub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.mihai.careerhub.entity.User;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}