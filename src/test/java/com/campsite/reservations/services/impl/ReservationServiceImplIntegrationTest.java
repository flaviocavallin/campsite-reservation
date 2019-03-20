package com.campsite.reservations.services.impl;

import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.campsite.reservations.controllers.domain.ReservationConfirmation;
import com.campsite.reservations.domain.Campsite;
import com.campsite.reservations.exceptions.CampsiteAlreadyReservedException;
import com.campsite.reservations.repositories.CampsiteRepository;
import com.campsite.reservations.repositories.CampsiteReservedDateRepository;
import com.campsite.reservations.repositories.ReservationRepository;
import com.campsite.reservations.services.ReservationService;
import com.campsite.reservations.test.utils.IntegrationBaseTest;

public class ReservationServiceImplIntegrationTest extends IntegrationBaseTest {

	private static final int MIN_BOOK_DAYS_AHEAD = 1;
	private static final int MAX_BOOK_DAYS_ADVANCE = 30;
	private static final int MAX_BOOK_NIGHTS = 3;
	private static final String CAMPSITE_NAME = "campsite1";

	@Autowired
	private ReservationService reservationService;

	@Autowired
	private CampsiteRepository campsiteRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private CampsiteReservedDateRepository campsiteReservedDateRepository;

	@Autowired
	private EntityManager entityManager;

	@Before
	public void setUp() {
		this.campsiteRepository
				.save(new Campsite(CAMPSITE_NAME, MIN_BOOK_DAYS_AHEAD, MAX_BOOK_DAYS_ADVANCE, MAX_BOOK_NIGHTS));
	}

	@Test
	public void reserveTest() {
		String email = "email@email.com";
		String name = "userName1";
		String surname = "userSurname1";
		Date checkinDate = DateUtils.addDays(new Date(), MIN_BOOK_DAYS_AHEAD);
		Date checkoutDate = DateUtils.addDays(checkinDate, MAX_BOOK_NIGHTS);

		ReservationConfirmation reservationConfirmation = reservationService.reserve(CAMPSITE_NAME, email, name,
				surname, checkinDate, checkoutDate);

		Assertions.assertThat(reservationConfirmation).isNotNull();

		long count = campsiteReservedDateRepository.count();

		Assertions.assertThat(count).isEqualTo(3);
	}

	@Test(expected = CampsiteAlreadyReservedException.class)
	public void overlappingReservationTest() {
		String email = "email@email.com";
		String name = "userName1";
		String surname = "userSurname1";
		Date checkinDate = DateUtils.addDays(new Date(), MIN_BOOK_DAYS_AHEAD);
		Date checkoutDate = DateUtils.addDays(checkinDate, MAX_BOOK_NIGHTS);

		ReservationConfirmation reservationConfirmation = reservationService.reserve(CAMPSITE_NAME, email, name,
				surname, checkinDate, checkoutDate);

		Assertions.assertThat(reservationConfirmation).isNotNull();

		entityManager.flush();
		entityManager.clear();

		Assertions.assertThat(this.reservationRepository.findByCode(reservationConfirmation.getReservationCode()))
				.isNotNull();

		long count = campsiteReservedDateRepository.count();
		Assertions.assertThat(count).isEqualTo(3);

		checkinDate = DateUtils.addDays(checkinDate, 2);
		checkoutDate = DateUtils.addDays(checkinDate, MAX_BOOK_NIGHTS);

		reservationService.reserve(CAMPSITE_NAME, email, name, surname, checkinDate, checkoutDate);
		entityManager.flush();
	}

	@Test
	public void cancelReservationTest() {
		String email = "email@email.com";
		String name = "userName1";
		String surname = "userSurname1";
		Date checkinDate = DateUtils.addDays(new Date(), MIN_BOOK_DAYS_AHEAD);
		Date checkoutDate = DateUtils.addDays(checkinDate, MAX_BOOK_NIGHTS);

		ReservationConfirmation reservationConfirmation = reservationService.reserve(CAMPSITE_NAME, email, name,
				surname, checkinDate, checkoutDate);

		String reservationCode = reservationConfirmation.getReservationCode();

		entityManager.flush();
		entityManager.clear();

		Assertions.assertThat(this.reservationRepository.findByCode(reservationCode)).isNotEmpty();
		Assertions.assertThat(this.campsiteReservedDateRepository.count()).isEqualTo(3);

		entityManager.clear();

		this.reservationService.cancelReservation(reservationCode);

		Assertions.assertThat(this.reservationRepository.findByCode(reservationCode)).isEmpty();
		Assertions.assertThat(this.campsiteReservedDateRepository.count()).isEqualTo(0);
	}

}