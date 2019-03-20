package com.campsite.reservations.repositories;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.campsite.reservations.domain.Campsite;
import com.campsite.reservations.domain.CampsiteReservedDate;
import com.campsite.reservations.test.utils.IntegrationBaseTest;

public class CampsiteReservedDateRepositoryIntegrationTest extends IntegrationBaseTest {

	@Autowired
	private CampsiteReservedDateRepository campsiteReservedDateRepository;

	@Autowired
	private CampsiteRepository campsiteRepository;

	private Campsite campsite;

	@Before
	public void setUp() {
		this.campsite = this.campsiteRepository.save(new Campsite("name1", 1, 30, 3));

		Date dateReserved = new Date();

		this.campsiteReservedDateRepository.save(new CampsiteReservedDate(campsite, dateReserved));
		this.campsiteReservedDateRepository
				.save(new CampsiteReservedDate(campsite, DateUtils.addDays(dateReserved, 1)));
	}

	@Test
	public void deleteByCampsiteIdTest() {
		Assertions.assertThat(this.campsiteReservedDateRepository.count()).isEqualTo(2);

		Date dateFrom = new Date();
		Date dateTo = DateUtils.addDays(dateFrom, 2);

		int rowsDeleted = this.campsiteReservedDateRepository.deleteByCampsiteIdAndBetweenDates(this.campsite.getId(),
				dateFrom, dateTo);

		Assertions.assertThat(rowsDeleted).isEqualTo(2);
		Assertions.assertThat(this.campsiteReservedDateRepository.count()).isEqualTo(0);
	}

	@Test
	public void getByCampsiteNameAndBetweenDatesTest() {
		Campsite campsite2 = this.campsiteRepository.save(new Campsite("name2", 1, 30, 3));
		this.campsiteReservedDateRepository.save(new CampsiteReservedDate(campsite2, new Date()));

		Date dateFrom = DateUtils.truncate(new Date(), Calendar.DATE);
		Date dateTo = DateUtils.truncate(DateUtils.addDays(new Date(), 1), Calendar.DATE);

		List<CampsiteReservedDate> list = this.campsiteReservedDateRepository
				.findByCampsiteNameAndBetweenDates(campsite.getName(), dateFrom, dateTo);

		Assertions.assertThat(list).size().isEqualTo(2);

		for (CampsiteReservedDate campsiteReservedDate : list) {
			Assertions.assertThat(campsiteReservedDate.getId().getCampsite()).isEqualTo(campsite);
			Assertions.assertThat(campsiteReservedDate.getId().getDateReserved()).isBetween(dateFrom, dateTo, true,
					true);
		}
	}
}
