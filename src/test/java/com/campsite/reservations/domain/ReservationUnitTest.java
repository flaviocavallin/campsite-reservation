package com.campsite.reservations.domain;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.Assertions;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import com.campsite.reservations.exceptions.ReservationIllegalArgumentException;

public class ReservationUnitTest {

	private Campsite campsite;
	private User user;

	@Before
	public void setUp() {
		this.user = new User("email@email.com", "name1", "surname1");
		this.campsite = new Campsite("campsite1", 1, 30, 3);
	}

	@Test
	public void successReservationTest() {
		Date checkinDate = DateUtils.addDays(new Date(), 1);
		Date checkoutDate = DateUtils.addDays(checkinDate, 3);
		Reservation reservation = new Reservation(campsite, user, checkinDate, checkoutDate);

		Assertions.assertThat(reservation.getUser()).isEqualTo(user);
		Assertions.assertThat(reservation.getCampsite()).isEqualTo(campsite);
		Assertions.assertThat(reservation.getCheckinDate()).isEqualTo(DateUtils.truncate(checkinDate, Calendar.DATE));
		Assertions.assertThat(reservation.getCheckoutDate()).isEqualTo(DateUtils.truncate(checkoutDate, Calendar.DATE));
	}

	@Test(expected = ReservationIllegalArgumentException.class)
	public void checkoutBeforeCheckinTest() {
		Date checkinDate = DateUtils.addDays(new Date(), 3);
		Date checkoutDate = DateUtils.addDays(new Date(), 1);
		new Reservation(campsite, user, checkinDate, checkoutDate);
	}

	@Test(expected = ReservationIllegalArgumentException.class)
	public void checkinDayLessThanMinBookDaysAheadTest() {
		Date checkinDate = new Date();
		Date checkoutDate = DateUtils.addDays(new Date(), 1);
		new Reservation(campsite, user, checkinDate, checkoutDate);
	}

	@Test(expected = ReservationIllegalArgumentException.class)
	public void checkinDayMoreThanMaxBookDaysAheadTest() {
		Date checkinDate = DateUtils.addDays(new Date(), 1);
		Date checkoutDate = DateUtils.addDays(new Date(), 33);
		new Reservation(campsite, user, checkinDate, checkoutDate);
	}

	@Test(expected = ReservationIllegalArgumentException.class)
	public void daysReservedMoreThanMaxBookNightsTest() {
		Date checkinDate = DateUtils.addDays(new Date(), 33);
		Date checkoutDate = DateUtils.addDays(new Date(), 34);
		new Reservation(campsite, user, checkinDate, checkoutDate);
	}

	@Test
	public void daysBetweenTest() {
		int amountOfDays = 3;
		Date checkinDate = new Date();
		Date checkoutDate = DateUtils.addDays(checkinDate, amountOfDays);

		int days = Days.daysBetween(new LocalDate(checkinDate.getTime()), new LocalDate(checkoutDate.getTime()))
				.getDays();

		Assertions.assertThat(days).isEqualTo(amountOfDays);
	}
}
