package com.campsite.reservations.repositories;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.campsite.reservations.domain.Campsite;
import com.campsite.reservations.test.utils.IntegrationBaseTest;

public class CampsiteRepositoryIntegrationTest extends IntegrationBaseTest {

	@Autowired
	private CampsiteRepository campsiteRepository;

	private Campsite campsite;

	@Before
	public void setUp() {
		this.campsite = this.campsiteRepository.save(new Campsite("name1", 1, 30, 3));
	}

	@Test
	public void findAllTest() {
		List<Campsite> it = this.campsiteRepository.findAll();
		Assertions.assertThat(it).containsExactly(campsite);
	}

	@Test
	public void findByNameTest() {
		Optional<Campsite> opCampsite = this.campsiteRepository.findByName(campsite.getName());
		Assertions.assertThat(opCampsite).isPresent();
		Assertions.assertThat(opCampsite.get()).isEqualTo(campsite);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void uniqueConstraintViolation() {
		this.campsiteRepository.save(new Campsite("name1", 1, 30, 3));
	}
}