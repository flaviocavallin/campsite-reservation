package com.campsite.reservations.services.impl;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import com.campsite.reservations.domain.Campsite;
import com.campsite.reservations.domain.CampsiteReservedDate;
import com.campsite.reservations.domain.Reservation;
import com.campsite.reservations.domain.User;
import com.campsite.reservations.exceptions.CampsiteAlreadyReservedException;
import com.campsite.reservations.repositories.CampsiteReservedDateRepository;
import com.campsite.reservations.services.CampsiteReservedDateService;

public class CampsiteReservedDateServiceImplUnitTest {

	private CampsiteReservedDateService campsiteReservedDateService;

	private CampsiteReservedDateRepository campsiteReservedDateRepository = Mockito
			.mock(CampsiteReservedDateRepository.class);

	private Reservation reservation;
	private User user;
	private Campsite campsite;

	@Before
	public void setUp() {
		this.campsiteReservedDateService = new CampsiteReservedDateServiceImpl(campsiteReservedDateRepository);
		this.user = new User("email@email.com", "name", "surname");
		this.campsite = new Campsite("campsite1", 1, 30, 3) {
			@Override
			public Integer getId() {
				return 1;
			}
		};
		this.reservation = new Reservation(campsite, user, DateUtils.addDays(new Date(), 1),
				DateUtils.addDays(new Date(), 4));

		Assertions.assertThat(this.reservation.getAmountReservedNights()).isEqualTo(3);
	}

	@Test
	public void reserveCampsiteTest() {
		ArgumentCaptor<CampsiteReservedDate> valueCapture = ArgumentCaptor.forClass(CampsiteReservedDate.class);

		Mockito.when(this.campsiteReservedDateRepository.saveAndFlush(valueCapture.capture()))
				.thenReturn(ArgumentMatchers.any(CampsiteReservedDate.class));

		campsiteReservedDateService.reserveCampsite(reservation);

		int i = -1;
		for (CampsiteReservedDate campsiteReservedDate : valueCapture.getAllValues()) {
			Assertions.assertThat(campsiteReservedDate.getId().getCampsite()).isEqualTo(reservation.getCampsite());
			Assertions.assertThat(campsiteReservedDate.getId().getDateReserved())
					.isEqualTo(DateUtils.addDays(reservation.getCheckinDate(), ++i));
		}
	}

	@Test(expected = CampsiteAlreadyReservedException.class)
	public void reserveCampsiteDataIntegrityViolationExceptionTest() {

		Mockito.when(this.campsiteReservedDateRepository.saveAndFlush(Mockito.any(CampsiteReservedDate.class)))
				.thenThrow(new DataIntegrityViolationException("The key already exists in the database"));

		campsiteReservedDateService.reserveCampsite(reservation);
	}

	@Test
	public void removeByCampsiteAndDateRangeTest() {
		Date dateFrom = new Date();
		Date dateTo = new Date();

		Mockito.when(
				campsiteReservedDateRepository.deleteByCampsiteIdAndBetweenDates(campsite.getId(), dateFrom, dateTo))
				.thenReturn(1);

		campsiteReservedDateService.removeByCampsiteAndDateRange(campsite, dateFrom, dateTo);

		Mockito.verify(campsiteReservedDateRepository).deleteByCampsiteIdAndBetweenDates(campsite.getId(), dateFrom,
				dateTo);
	}
}