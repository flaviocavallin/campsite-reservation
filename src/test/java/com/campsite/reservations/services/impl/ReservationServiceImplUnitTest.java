package com.campsite.reservations.services.impl;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.campsite.reservations.controllers.domain.ReservationConfirmation;
import com.campsite.reservations.domain.Campsite;
import com.campsite.reservations.domain.Reservation;
import com.campsite.reservations.domain.User;
import com.campsite.reservations.repositories.CampsiteRepository;
import com.campsite.reservations.repositories.ReservationRepository;
import com.campsite.reservations.repositories.UserRepository;
import com.campsite.reservations.services.CampsiteReservedDateService;
import com.campsite.reservations.services.ReservationService;

public class ReservationServiceImplUnitTest {

	private ReservationService reservationService;

	private ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);
	private UserRepository userRepository = Mockito.mock(UserRepository.class);
	private CampsiteRepository campsiteRepository = Mockito.mock(CampsiteRepository.class);
	private CampsiteReservedDateService campsiteReservedDateService = Mockito.mock(CampsiteReservedDateService.class);

	@Before
	public void setUp() {
		this.reservationService = new ReservationServiceImpl(reservationRepository, userRepository, campsiteRepository,
				campsiteReservedDateService);
	}

	@Test
	public void reserveTest() {
		String campsiteName = "campsiteName1";
		String email = "email@mail.com";
		String name = "name1";
		String surname = "surname1";
		Date checkinDate = new Date();
		Date checkoutDate = new Date();

		User user = Mockito.mock(User.class);

		Mockito.when(user.getEmail()).thenReturn(email);
		Mockito.when(user.getName()).thenReturn(name);
		Mockito.when(user.getSurname()).thenReturn(surname);

		Campsite campsite = Mockito.mock(Campsite.class);

		Reservation reservation = new Reservation(campsite, user, checkinDate, checkoutDate);

		Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		Mockito.when(campsiteRepository.findByName(campsiteName)).thenReturn(Optional.of(campsite));

		Mockito.when(reservationRepository.save(ArgumentMatchers.any(Reservation.class))).thenReturn(reservation);

		Mockito.doNothing().when(campsiteReservedDateService).reserveCampsite(reservation);

		ReservationConfirmation reservationConfirmation = this.reservationService.reserve(campsiteName, email, name,
				surname, checkinDate, checkoutDate);

		Assertions.assertThat(reservationConfirmation.getCampsiteName()).isEqualTo(campsiteName);
		Assertions.assertThat(reservationConfirmation.getReservationCode()).isEqualTo(reservation.getCode());
		Assertions.assertThat(reservationConfirmation.getEmail()).isEqualTo(email);
		Assertions.assertThat(reservationConfirmation.getName()).isEqualTo(name);
		Assertions.assertThat(reservationConfirmation.getSurname()).isEqualTo(surname);
		Assertions.assertThat(reservationConfirmation.getCheckinDate()).isEqualTo(reservation.getCheckinDate());
		Assertions.assertThat(reservationConfirmation.getCheckoutDate()).isEqualTo(reservation.getCheckoutDate());
	}

	@Test
	public void cancelReservationTest() {
		String reservationCode = "code1";

		Reservation reservation = Mockito.mock(Reservation.class);
		Mockito.when(reservation.getCheckinDate()).thenReturn(new Date());
		Mockito.when(reservation.getCheckoutDate()).thenReturn(new Date());

		Campsite campsite = Mockito.mock(Campsite.class);

		Mockito.when(reservationRepository.findByCode(reservationCode)).thenReturn(Optional.of(reservation));
		Mockito.when(reservation.getCampsite()).thenReturn(campsite);

		reservationService.cancelReservation(reservationCode);

		Mockito.verify(campsiteReservedDateService).removeByCampsiteAndDateRange(campsite, reservation.getCheckinDate(),
				reservation.getCheckoutDate());
		Mockito.verify(reservationRepository).delete(reservation);
	}

	@Test
	public void modifyTest() {
		String campsiteName = "campsite1";
		String email = "email@mail.com";
		String name = "name1";
		String surname = "surname1";

		Date newCheckinDate = DateUtils.addDays(new Date(), 3);
		Date newCheckoutDate = DateUtils.addDays(new Date(), 4);

		Campsite campsite = new Campsite(campsiteName, 1, 30, 3);
		User user = new User(email, name, surname);

		Reservation reservation = new Reservation(campsite, user, DateUtils.addDays(new Date(), 1),
				DateUtils.addDays(new Date(), 2));

		Mockito.doNothing().when(campsiteReservedDateService).reserveCampsite(reservation);

		Mockito.when(reservationRepository.findByCode(reservation.getCode())).thenReturn(Optional.of(reservation));

		ReservationConfirmation reservationConfirmation = reservationService.modify(reservation.getCode(),
				newCheckinDate, newCheckoutDate);

		Assertions.assertThat(reservationConfirmation.getCampsiteName()).isEqualTo(campsiteName);
		Assertions.assertThat(reservationConfirmation.getReservationCode()).isEqualTo(reservation.getCode());
		Assertions.assertThat(reservationConfirmation.getEmail()).isEqualTo(email);
		Assertions.assertThat(reservationConfirmation.getName()).isEqualTo(name);
		Assertions.assertThat(reservationConfirmation.getSurname()).isEqualTo(surname);
		Assertions.assertThat(reservationConfirmation.getCheckinDate()).isEqualTo(newCheckinDate);
		Assertions.assertThat(reservationConfirmation.getCheckoutDate()).isEqualTo(newCheckoutDate);
	}
}
