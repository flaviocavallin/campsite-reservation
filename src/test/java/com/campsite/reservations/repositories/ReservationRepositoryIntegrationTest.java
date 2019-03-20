package com.campsite.reservations.repositories;

import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.campsite.reservations.domain.Campsite;
import com.campsite.reservations.domain.Reservation;
import com.campsite.reservations.domain.User;
import com.campsite.reservations.test.utils.IntegrationBaseTest;

public class ReservationRepositoryIntegrationTest extends IntegrationBaseTest {

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private CampsiteRepository campsiteRepository;

	@Autowired
	private EntityManager entityManager;

	private Reservation reservation;

	@Before
	public void setUp() {
		Campsite campsite = this.campsiteRepository.save(new Campsite("campsite1", 1, 30, 3));
		User user = new User("email@mail.com", "name1", "surname1");

		Date checkinDate = DateUtils.addDays(new Date(), 1);
		Date checkoutDate = DateUtils.addDays(checkinDate, 3);

		reservation = reservationRepository.save(new Reservation(campsite, user, checkinDate, checkoutDate));

		entityManager.flush();
		entityManager.clear();
	}

	@Test
	public void findByCodeTest() {
		Reservation res = reservationRepository.findByCode(reservation.getCode()).get();
		Assertions.assertThat(res.getCode()).isEqualTo(reservation.getCode());
	}
}