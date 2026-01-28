package com.sipel.backend.repositories;

import com.sipel.backend.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<Users, String> {

    UserDetails findByEmail(String email);

    boolean existsByEmail(String email);
}
