package com.campsite.reservations.services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campsite.reservations.controllers.domain.CampsiteAvailability;
import com.campsite.reservations.domain.Campsite;
import com.campsite.reservations.domain.CampsiteReservedDate;
import com.campsite.reservations.repositories.CampsiteRepository;
import com.campsite.reservations.repositories.CampsiteReservedDateRepository;
import com.campsite.reservations.services.CampsiteService;

@Service
class CampsiteServiceImpl implements CampsiteService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CampsiteServiceImpl.class);

	private CampsiteRepository campsiteRepository;
	private CampsiteReservedDateRepository campsiteReservedDateRepository;
	private String defaultCampsiteName;
	private int defaultminBookDaysAhead;
	private int defaultmaxBookDaysInAdvance;
	private int defaultCampsiteMaxBookNights;

	CampsiteServiceImpl(CampsiteRepository campsiteRepository,
			CampsiteReservedDateRepository campsiteReservedDateRepositor,
			@Value("${default.campsite.name}") String defaultCampsiteName,
			@Value("${default.campsite.minBookDaysAhead}") int defaultminBookDaysAhead,
			@Value("${default.campsite.maxBookDaysInAdvance}") int defaultmaxBookDaysInAdvance,
			@Value("${default.campsite.maxBookNights}") int defaultCampsiteMaxBookNights) {

		this.campsiteRepository = Objects.requireNonNull(campsiteRepository, "campsiteRepository can not be null");
		this.campsiteReservedDateRepository = Objects.requireNonNull(campsiteReservedDateRepositor,
				"campsiteReservedDateRepositor can not be null");
		this.defaultCampsiteName = Objects.requireNonNull(defaultCampsiteName, "defaultCampsiteName can not be null");
		this.defaultminBookDaysAhead = defaultminBookDaysAhead;
		this.defaultmaxBookDaysInAdvance = defaultmaxBookDaysInAdvance;
		this.defaultCampsiteMaxBookNights = defaultCampsiteMaxBookNights;
	}

	@Override
	@Transactional(readOnly = false)
	@CacheEvict(value = "campsite", key = "#p0")
	public void create(String name, int minBookDaysAhead, int maxBookDaysInAdvance, int maxBookNights) {
		Validate.notBlank(name, "name can not be blank");

		LOGGER.debug(
				"Trying to create a campsite with name={}, minBookDaysAhead={}, maxBookDaysInAdvance={}, maxBookNights={}",
				name, minBookDaysAhead, maxBookDaysInAdvance, maxBookNights);

		Campsite campsite = this.campsiteRepository
				.save(new Campsite(name, minBookDaysAhead, maxBookDaysInAdvance, maxBookNights));

		LOGGER.info("Campsite created={}", campsite);
	}

	@Override
	@Transactional(readOnly = false)
	public void createDefaultIfNotExists() {
		Optional<Campsite> opCampsite = this.campsiteRepository.findByName(defaultCampsiteName);

		if (!opCampsite.isPresent()) {
			this.create(defaultCampsiteName, defaultminBookDaysAhead, defaultmaxBookDaysInAdvance,
					defaultCampsiteMaxBookNights);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public CampsiteAvailability getAvailability(String campsiteName, Date requiredDateFrom, Date requiredDateTo) {
		Validate.notBlank(campsiteName, "campsiteName can not be blank");
		Validate.notNull(requiredDateFrom, "requiredDateFrom can not be null");
		Validate.notNull(requiredDateTo, "requiredDateTo can not be null");

		Date dateFrom = DateUtils.truncate(requiredDateFrom, Calendar.DATE);
		Date dateTo = DateUtils.truncate(requiredDateTo, Calendar.DATE);

		Validate.isTrue(dateTo.compareTo(dateFrom) <= 1, "DateTo=%s has to be less or equals DateFrom=%s", dateTo,
				dateFrom);

		List<CampsiteReservedDate> list = campsiteReservedDateRepository.findByCampsiteNameAndBetweenDates(campsiteName,
				dateFrom, dateTo);

		Set<Date> reservedDates = list.stream().map(d -> d.getId().getDateReserved()).collect(Collectors.toSet());

		CampsiteAvailability campsiteAvailability = new CampsiteAvailability(campsiteName, dateFrom, dateTo);

		Date availableDate = new Date(dateFrom.getTime());

		while (availableDate.compareTo(dateTo) < 1) {
			if (!reservedDates.contains(availableDate)) {
				campsiteAvailability.addAvailableDate(availableDate);
			}
			availableDate = DateUtils.addDays(availableDate, 1);
		}

		return campsiteAvailability;
	}

}
