package com.jobportal.user_service.Repositories;

import com.jobportal.user_service.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User , Long> {
    Optional<User> findByEmail(String email);
    @Transactional
    void deleteByEmail(String email);
}
