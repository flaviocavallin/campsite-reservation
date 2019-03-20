package com.campsite.reservations.domain;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class CampsiteReservedDateUnitTest {

	private Campsite campsite;

	@Before
	public void setUp() {
		this.campsite = new Campsite("name1", 1, 30, 3);
	}

	@Test
	public void campsiteReservedDateTest() {
		Assertions.assertThat(this.campsite.getCampsiteReservedDates()).isEmpty();

		Date dateReserved = DateUtils.addDays(new Date(), this.campsite.getMinBookDaysAhead());

		CampsiteReservedDate campsiteReservedDate = new CampsiteReservedDate(this.campsite, dateReserved);

		Assertions.assertThat(campsiteReservedDate.getId().getCampsite())
				.isEqualToComparingFieldByFieldRecursively(campsite);
		Assertions.assertThat(campsiteReservedDate.getId().getDateReserved())
				.isEqualTo(DateUtils.truncate(dateReserved, Calendar.DATE));

		Assertions.assertThat(this.campsite.getCampsiteReservedDates()).isNotEmpty();
		Assertions.assertThat(this.campsite.getCampsiteReservedDates().get(0)).isEqualTo(campsiteReservedDate);
	}
}
