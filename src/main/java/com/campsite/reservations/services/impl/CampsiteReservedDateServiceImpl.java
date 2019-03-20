package com.campsite.reservations.services.impl;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campsite.reservations.domain.Campsite;
import com.campsite.reservations.domain.CampsiteReservedDate;
import com.campsite.reservations.domain.Reservation;
import com.campsite.reservations.exceptions.CampsiteAlreadyReservedException;
import com.campsite.reservations.repositories.CampsiteReservedDateRepository;
import com.campsite.reservations.services.CampsiteReservedDateService;

@Service
class CampsiteReservedDateServiceImpl implements CampsiteReservedDateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CampsiteReservedDateServiceImpl.class);

	private CampsiteReservedDateRepository campsiteReservedDateRepository;

	CampsiteReservedDateServiceImpl(CampsiteReservedDateRepository campsiteReservedDateRepository) {
		this.campsiteReservedDateRepository = Objects.requireNonNull(campsiteReservedDateRepository,
				"campsiteReservedDateRepository can not be null");
	}

	@Override
	@Transactional(readOnly = false)
	public void reserveCampsite(Reservation reservation) {
		Validate.notNull(reservation, "reservation can not be null");

		try {
			for (int i = 0; i < reservation.getAmountReservedNights(); i++) {
				Date dateReserved = DateUtils.addDays(reservation.getCheckinDate(), i);
				this.campsiteReservedDateRepository
						.saveAndFlush(new CampsiteReservedDate(reservation.getCampsite(), dateReserved));
			}
		} catch (DataIntegrityViolationException e) {
			LOGGER.error(
					"The userEmail={} was trying to reserve the campsiteId={} that was already reserved between checkinDate={} and checkoutDate={}",
					reservation.getUser().getEmail(), reservation.getCampsite().getId(), reservation.getCheckinDate(),
					reservation.getCheckoutDate(), e);

			throw new CampsiteAlreadyReservedException(String.format(
					"Exist one reservation for the campsite=%s and dates between %s and %s",
					reservation.getCampsite().getName(), reservation.getCheckinDate(), reservation.getCheckoutDate()));
		}
	}

	@Override
	public void removeByCampsiteAndDateRange(Campsite campsite, Date dateFrom, Date dateTo) {
		Validate.notNull(campsite, "campsite can not be null");
		Validate.notNull(dateFrom, "dateFrom can not be null");
		Validate.notNull(dateTo, "dateTo can not be null");

		int rowsDeleted = this.campsiteReservedDateRepository.deleteByCampsiteIdAndBetweenDates(campsite.getId(),
				dateFrom, dateTo);

		LOGGER.debug("removeByCampsiteAndDateRange Rows={} deleted for campsiteId={}, dateFrom={}, dateTo={}",
				rowsDeleted, campsite.getId(), dateFrom, dateTo);
	}
}
