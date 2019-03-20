package com.campsite.reservations.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.campsite.reservations.domain.Campsite;

@Repository
public interface CampsiteRepository extends JpaRepository<Campsite, Integer> {

	Optional<Campsite> findByName(String name);

}