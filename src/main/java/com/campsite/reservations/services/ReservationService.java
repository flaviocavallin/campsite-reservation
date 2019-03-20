package com.campsite.reservations.services;

import java.util.Date;

import com.campsite.reservations.controllers.domain.ReservationConfirmation;

public interface ReservationService {

	/**
	 * This method will do the reservation
	 * 
	 * @param String
	 *            campsiteName
	 * @param String
	 *            email
	 * @param String
	 *            name
	 * @param String
	 *            surname
	 * @param Date
	 *            checkinDate
	 * @param Date
	 *            checkoutDate
	 * 
	 * @return {@code ReservationConfirmation}
	 */

	ReservationConfirmation reserve(String campsiteName, String email, String name, String surname, Date checkinDate,
			Date checkoutDate);

	/**
	 * This method will modify the reservation dateFrom and newCheckinDate by
	 * the given newCheckinDate
	 * 
	 * @param String
	 *            reservationCode
	 * @param Date
	 *            newCheckinDate
	 * @param Date
	 *            newCheckoutDate
	 * @return {@code ReservationConfirmation}
	 */
	ReservationConfirmation modify(String reservationCode, Date newCheckinDate, Date newCheckoutDate);

	/**
	 * This method will cancel the reservation
	 * 
	 * @param String
	 *            reservationCode
	 */
	void cancelReservation(String reservationCode);
}
