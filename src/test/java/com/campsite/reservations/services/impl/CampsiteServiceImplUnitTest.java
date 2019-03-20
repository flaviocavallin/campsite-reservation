package com.campsite.reservations.services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.campsite.reservations.controllers.domain.CampsiteAvailability;
import com.campsite.reservations.domain.Campsite;
import com.campsite.reservations.domain.CampsiteReservedDate;
import com.campsite.reservations.repositories.CampsiteRepository;
import com.campsite.reservations.repositories.CampsiteReservedDateRepository;
import com.campsite.reservations.services.CampsiteService;

public class CampsiteServiceImplUnitTest {

	private CampsiteService campsiteService;

	private CampsiteRepository campsiteRepository = Mockito.mock(CampsiteRepository.class);
	private CampsiteReservedDateRepository campsiteReservedDateRepository = Mockito
			.mock(CampsiteReservedDateRepository.class);
	private String defaultCampsiteName = "defaultCampsiteName";
	private int defaultminBookDaysAhead = 1;
	private int defaultmaxBookDaysInAdvance = 30;
	private int defaultCampsiteMaxBookNights = 3;

	@Before
	public void setUp() {
		this.campsiteService = new CampsiteServiceImpl(campsiteRepository, campsiteReservedDateRepository,
				defaultCampsiteName, defaultminBookDaysAhead, defaultmaxBookDaysInAdvance,
				defaultCampsiteMaxBookNights);
	}

	@Test
	public void getAvailabilityTest() {
		String campsiteName = "campsiteName";
		Date dateFrom = new Date();
		Date dateTo = DateUtils.addDays(dateFrom, 5);

		Campsite campsite = new Campsite(campsiteName, 1, 30, 3);

		CampsiteReservedDate campsiteReservedDate1 = new CampsiteReservedDate(campsite, new Date());
		CampsiteReservedDate campsiteReservedDate2 = new CampsiteReservedDate(campsite,
				DateUtils.addDays(new Date(), 1));

		List<CampsiteReservedDate> list = new LinkedList<>();
		list.add(campsiteReservedDate1);
		list.add(campsiteReservedDate2);

		Date dateFromTruncated = DateUtils.truncate(dateFrom, Calendar.DATE);
		Date dateToTruncated = DateUtils.truncate(dateTo, Calendar.DATE);

		Mockito.when(campsiteReservedDateRepository.findByCampsiteNameAndBetweenDates(campsiteName, dateFromTruncated,
				dateToTruncated)).thenReturn(list);

		CampsiteAvailability campsiteAvailability = this.campsiteService.getAvailability(campsiteName, dateFrom,
				dateTo);

		Assertions.assertThat(campsiteAvailability).isNotNull();
		Assertions.assertThat(campsiteAvailability.getCampsiteName()).isEqualTo(campsiteName);
		Assertions.assertThat(campsiteAvailability.getDateFrom()).isEqualTo(dateFromTruncated);
		Assertions.assertThat(campsiteAvailability.getDateTo()).isEqualTo(dateToTruncated);

		Assertions.assertThat(campsiteAvailability.getAvailableDates()).containsExactly(
				DateUtils.addDays(dateFromTruncated, 2), DateUtils.addDays(dateFromTruncated, 3),
				DateUtils.addDays(dateFromTruncated, 4), DateUtils.addDays(dateFromTruncated, 5));

		Mockito.verify(campsiteReservedDateRepository).findByCampsiteNameAndBetweenDates(campsiteName,
				dateFromTruncated, dateToTruncated);
	}

	@Test
	public void createTest() {
		String name = "campsite1";
		int minBookDaysAhead = 1;
		int maxBookDaysInAdvance = 30;
		int maxBookNights = 3;

		ArgumentCaptor<Campsite> valueCapture = ArgumentCaptor.forClass(Campsite.class);

		Mockito.when(campsiteRepository.save(valueCapture.capture())).thenReturn(ArgumentMatchers.any(Campsite.class));

		this.campsiteService.create(name, minBookDaysAhead, maxBookDaysInAdvance, maxBookNights);

		Campsite campsite = valueCapture.getValue();
		Assertions.assertThat(campsite.getName()).isEqualTo(name);
		Assertions.assertThat(campsite.getMinBookDaysAhead()).isEqualTo(minBookDaysAhead);
		Assertions.assertThat(campsite.getMaxBookDaysInAdvance()).isEqualTo(maxBookDaysInAdvance);
		Assertions.assertThat(campsite.getMaxBookNights()).isEqualTo(maxBookNights);

		Mockito.verify(campsiteRepository).save(valueCapture.capture());
	}

	@Test
	public void createDefaultIfNotExistsTest() {

		Mockito.when(campsiteRepository.findByName(defaultCampsiteName)).thenReturn(Optional.ofNullable(null));

		ArgumentCaptor<Campsite> valueCapture = ArgumentCaptor.forClass(Campsite.class);

		Mockito.when(campsiteRepository.save(valueCapture.capture())).thenReturn(ArgumentMatchers.any(Campsite.class));

		this.campsiteService.createDefaultIfNotExists();

		Campsite campsite = valueCapture.getValue();
		Assertions.assertThat(campsite.getName()).isEqualTo(defaultCampsiteName);
		Assertions.assertThat(campsite.getMinBookDaysAhead()).isEqualTo(defaultminBookDaysAhead);
		Assertions.assertThat(campsite.getMaxBookDaysInAdvance()).isEqualTo(defaultmaxBookDaysInAdvance);
		Assertions.assertThat(campsite.getMaxBookNights()).isEqualTo(defaultCampsiteMaxBookNights);

		Mockito.verify(campsiteRepository).findByName(defaultCampsiteName);
	}
}