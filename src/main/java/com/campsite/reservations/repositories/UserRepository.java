package com.campsite.reservations.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campsite.reservations.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByEmail(String email);
}