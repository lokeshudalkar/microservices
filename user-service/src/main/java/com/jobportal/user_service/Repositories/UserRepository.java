package com.jobportal.user_service.Repositories;

import com.jobportal.user_service.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * The interface User repository.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    //ALTER TABLE users
    //MODIFY email VARCHAR(255) COLLATE utf8mb4_bin;
    // for making email case-sensitive

    /**
     * Find by email optional.
     *
     * @param email the email
     * @return the optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Delete by email.
     *
     * @param email the email
     */
    @Transactional
    void deleteByEmail(String email);
}
