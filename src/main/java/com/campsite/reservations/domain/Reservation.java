package com.campsite.reservations.domain;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.campsite.reservations.exceptions.ReservationIllegalArgumentException;

@Entity
@Table(name = "RESERVATIONS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Reservation extends AbstractEntity<Long> {

	private static final long serialVersionUID = -3176517520687944495L;

	private static final Logger LOGGER = LoggerFactory.getLogger(Reservation.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Campsite campsite;

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private User user;

	@Column(name = "RESERVATION_CODE", unique = true)
	private String code;

	@Temporal(TemporalType.DATE)
	private Date checkinDate;

	@Temporal(TemporalType.DATE)
	private Date checkoutDate;

	Reservation() {
		// do nothing for hydration purpose
	}

	public Reservation(Campsite campsite, User user, Date checkinDate, Date checkoutDate) {
		this();
		Validate.notNull(campsite, "campsite can not be null");
		Validate.notNull(user, "user can not be null");
		Validate.notNull(checkinDate, "checkinDate can not be null");
		Validate.notNull(checkoutDate, "checkoutDate can not be null");

		this.campsite = campsite;
		this.user = user;
		this.code = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
		this.setReservationDates(checkinDate, checkoutDate);
	}

	public void setReservationDates(Date checkinDate, Date checkoutDate) {
		Validate.notNull(checkinDate, "checkinDate can not be null");
		Validate.notNull(checkoutDate, "checkoutDate can not be null");

		if (checkinDate.compareTo(checkoutDate) >= 1) {
			LOGGER.error("checkinDate={} has to be less or equals to checkoutDate={}", checkinDate, checkoutDate);

			throw new ReservationIllegalArgumentException(String
					.format("checkinDate=%s has to be less or equals to checkoutDate=%s", checkinDate, checkoutDate));
		}

		int amountReservedNights = Days
				.daysBetween(new LocalDate(checkinDate.getTime()), new LocalDate(checkoutDate.getTime())).getDays();

		if (amountReservedNights > campsite.getMaxBookNights()) {
			LOGGER.error("Trying to to a reservation with checkinDate={} and checkoutDate={} that is greater than {}",
					checkinDate, checkoutDate, campsite.getMaxBookNights());

			throw new ReservationIllegalArgumentException(String.format(
					"Trying to to a reservation with checkinDate=%s and checkoutDate=%s that is greater than %s",
					checkinDate, checkoutDate, campsite.getMaxBookNights()));
		}

		int amountDaysAhead = Days.daysBetween(new LocalDate().now(), new LocalDate(checkinDate.getTime())).getDays();

		if (amountDaysAhead < campsite.getMinBookDaysAhead()) {
			LOGGER.error("CheckinDate={} has to be at least {} days ahead before arrival", checkinDate,
					campsite.getMinBookDaysAhead());

			throw new ReservationIllegalArgumentException(
					String.format("CheckinDate=%s has to be at least %s days ahead before arrival", checkinDate,
							campsite.getMinBookDaysAhead()));
		}

		int amountDaysInAdvance = Days.daysBetween(new LocalDate().now(), new LocalDate(checkinDate.getTime()))
				.getDays();

		if (amountDaysInAdvance > campsite.getMaxBookDaysInAdvance()) {
			LOGGER.error("CheckinDate={} can not be more than {} days in advance", checkinDate,
					campsite.getMaxBookDaysInAdvance());

			throw new ReservationIllegalArgumentException(
					String.format("CheckinDate=%s can not be more than %s days in advance", checkinDate,
							campsite.getMaxBookDaysInAdvance()));
		}

		this.checkinDate = DateUtils.truncate(new Date(checkinDate.getTime()), Calendar.DATE);
		this.checkoutDate = DateUtils.truncate(new Date(checkoutDate.getTime()), Calendar.DATE);
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public Campsite getCampsite() {
		return campsite;
	}

	public User getUser() {
		return user;
	}

	public String getCode() {
		return code;
	}

	public Date getCheckinDate() {
		return checkinDate == null ? null : new Date(checkinDate.getTime());
	}

	public Date getCheckoutDate() {
		return checkoutDate == null ? null : new Date(checkoutDate.getTime());
	}

	public int getAmountReservedNights() {
		return Days.daysBetween(new LocalDate(checkinDate.getTime()), new LocalDate(checkoutDate.getTime())).getDays();
	}

}