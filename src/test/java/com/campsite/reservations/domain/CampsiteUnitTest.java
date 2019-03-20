package com.campsite.reservations.domain;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class CampsiteUnitTest {

	@Test
	public void campsiteTest() {
		String name = "name1";
		int minBookDaysAhead = 1;
		int maxBookDaysInAdvance = 30;
		int maxBookNights = 3;

		Campsite campsite = new Campsite(name, minBookDaysAhead, maxBookDaysInAdvance, maxBookNights);

		Assertions.assertThat(campsite.getName()).isEqualTo(name);
		Assertions.assertThat(campsite.getMinBookDaysAhead()).isEqualTo(minBookDaysAhead);
		Assertions.assertThat(campsite.getMaxBookDaysInAdvance()).isEqualTo(maxBookDaysInAdvance);
		Assertions.assertThat(campsite.getMaxBookNights()).isEqualTo(maxBookNights);
	}
}