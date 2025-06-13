package com.irctc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.irctc.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	 boolean existsByUsername(String username);
	  boolean existsByEmail(String email);
	  Optional<User> findByEmail(String email);
	  Optional<User> findByUsername(String username);


}

