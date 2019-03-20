package com.campsite.reservations.services.impl;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campsite.reservations.controllers.domain.ReservationConfirmation;
import com.campsite.reservations.domain.Campsite;
import com.campsite.reservations.domain.Reservation;
import com.campsite.reservations.domain.User;
import com.campsite.reservations.exceptions.ReservationIllegalArgumentException;
import com.campsite.reservations.repositories.CampsiteRepository;
import com.campsite.reservations.repositories.ReservationRepository;
import com.campsite.reservations.repositories.UserRepository;
import com.campsite.reservations.services.CampsiteReservedDateService;
import com.campsite.reservations.services.ReservationService;

@Service
class ReservationServiceImpl implements ReservationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReservationServiceImpl.class);

	private ReservationRepository reservationRepository;
	private UserRepository userRepository;
	private CampsiteRepository campsiteRepository;
	private CampsiteReservedDateService campsiteReservedDateService;

	ReservationServiceImpl(ReservationRepository reservationRepository, UserRepository userRepository,
			CampsiteRepository campsiteRepository, CampsiteReservedDateService campsiteReservedDateService) {

		this.reservationRepository = Objects.requireNonNull(reservationRepository,
				"reservationRepository can not be null");
		this.userRepository = Objects.requireNonNull(userRepository, "userRepository can not be null");
		this.campsiteRepository = Objects.requireNonNull(campsiteRepository, "campsiteRepository can not be null");
		this.campsiteReservedDateService = Objects.requireNonNull(campsiteReservedDateService,
				"campsiteReservedDateService can not be null");
	}

	@Override
	@Transactional(readOnly = false)
	public ReservationConfirmation reserve(String campsiteName, String email, String name, String surname,
			Date checkinDate, Date checkoutDate) {

		Validate.notBlank(campsiteName, "campsiteName can not be blank");
		Validate.notBlank(email, "email can not be blank");
		Validate.notBlank(name, "name can not be blank");
		Validate.notBlank(surname, "surname can not be blank");
		Validate.notNull(checkinDate, "checkinDate can not be null");
		Validate.notNull(checkoutDate, "checkoutDate can not be null");

		Optional<User> opUser = this.userRepository.findByEmail(email);

		User user = opUser.orElseGet(() -> new User(email, name, surname));

		Campsite campsite = this.campsiteRepository.findByName(campsiteName)
				.orElseThrow(() -> new ReservationIllegalArgumentException("The campsite selected do not exist"));

		Reservation reservation = this.reservationRepository
				.save(new Reservation(campsite, user, checkinDate, checkoutDate));

		this.campsiteReservedDateService.reserveCampsite(reservation);

		return new ReservationConfirmation(campsiteName, reservation.getCode(), user.getEmail(), user.getName(),
				user.getSurname(), reservation.getCheckinDate(), reservation.getCheckoutDate());
	}

	@Override
	@Transactional(readOnly = false)
	@CacheEvict(value = "reservations", key = "#p0", beforeInvocation = false)
	public void cancelReservation(String reservationCode) {
		Validate.notBlank(reservationCode, "reservationCode can not be blank");

		Reservation reservation = this.reservationRepository.findByCode(reservationCode)
				.orElseThrow(() -> new ReservationIllegalArgumentException(
						String.format("Reservation code=%s not exists", reservationCode)));

		Campsite campsite = reservation.getCampsite();

		this.campsiteReservedDateService.removeByCampsiteAndDateRange(campsite, reservation.getCheckinDate(),
				reservation.getCheckoutDate());

		this.reservationRepository.delete(reservation);
	}

	@Override
	@Transactional(readOnly = false)
	@CacheEvict(value = "reservations", key = "#p0", beforeInvocation = false)
	public ReservationConfirmation modify(String reservationCode, Date newCheckinDate, Date newCheckoutDate) {
		Validate.notBlank(reservationCode, "reservationCode can not be blank");
		Validate.notNull(newCheckinDate, "newCheckinDate can not be null");
		Validate.notNull(newCheckoutDate, "newCheckoutDate can not be null");

		Reservation reservation = this.reservationRepository.findByCode(reservationCode)
				.orElseThrow(() -> new ReservationIllegalArgumentException(
						String.format("Reservation code=%s not exists", reservationCode)));

		this.campsiteReservedDateService.removeByCampsiteAndDateRange(reservation.getCampsite(),
				reservation.getCheckinDate(), reservation.getCheckoutDate());

		reservation.setReservationDates(newCheckinDate, newCheckoutDate);
		this.campsiteReservedDateService.reserveCampsite(reservation);

		ReservationConfirmation reservationConfirmation = new ReservationConfirmation(
				reservation.getCampsite().getName(), reservationCode, reservation.getUser().getEmail(),
				reservation.getUser().getName(), reservation.getUser().getSurname(), newCheckinDate, newCheckoutDate);

		return reservationConfirmation;
	}

}