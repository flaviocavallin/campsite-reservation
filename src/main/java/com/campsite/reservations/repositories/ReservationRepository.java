package com.campsite.reservations.repositories;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.campsite.reservations.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@Cacheable(value = "reservations", unless = "#result==null")
	Optional<Reservation> findByCode(String code);
}