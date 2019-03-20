package com.campsite.reservations.controllers;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.campsite.reservations.controllers.domain.CampsiteAvailability;
import com.campsite.reservations.services.CampsiteService;

@RunWith(SpringRunner.class)
@WebMvcTest(CampsiteController.class)
@ActiveProfiles("test")
public class CampsiteControllerTest {

	@MockBean
	private CampsiteService campsiteService;

	@Autowired
	private MockMvc mvc;

	@Value("${default.campsite.name}")
	private String defaultCampsiteName;

	private FastDateFormat dateFormat = FastDateFormat.getInstance("dd-MM-yyyy");

	@Test
	public void getAvailabilityWithDefaultTest() throws Exception {
		Date dateFrom = DateUtils.truncate(new Date(), Calendar.DATE);
		Date dateTo = DateUtils.addMonths(dateFrom, 1);

		CampsiteAvailability campsiteAvailability = new CampsiteAvailability(defaultCampsiteName, dateFrom, dateTo);
		campsiteAvailability.addAvailableDate(new Date());

		given(campsiteService.getAvailability(ArgumentMatchers.anyString(), ArgumentMatchers.any(Date.class),
				ArgumentMatchers.any(Date.class))).willReturn(campsiteAvailability);

		mvc.perform(get("/campsites/availability").contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.campsiteName", is(campsiteAvailability.getCampsiteName())))
				.andExpect(jsonPath("$.dateFrom", is(dateFormat.format(campsiteAvailability.getDateFrom()))))
				.andExpect(jsonPath("$.dateTo", is(dateFormat.format(campsiteAvailability.getDateTo()))))
				.andExpect(jsonPath("$.availableDates[0]",
						is(dateFormat.format(campsiteAvailability.getAvailableDates().get(0)))));

	}

	@Test
	public void getAvailabilityWithParametersTest() throws Exception {
		String campsiteName = "campsite1";
		String dateFrom = "01-01-2018";
		String dateTo = "03-01-2018";

		CampsiteAvailability campsiteAvailability = new CampsiteAvailability(campsiteName, dateFormat.parse(dateFrom),
				dateFormat.parse(dateTo));
		campsiteAvailability.addAvailableDate(new Date());

		given(campsiteService.getAvailability(campsiteName, dateFormat.parse(dateFrom), dateFormat.parse(dateTo)))
				.willReturn(campsiteAvailability);

		mvc.perform(get("/campsites/availability").param("campsiteName", campsiteName).param("dateFrom", dateFrom)
				.param("dateTo", dateTo).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.campsiteName", is(campsiteAvailability.getCampsiteName())))
				.andExpect(jsonPath("$.dateFrom", is(dateFormat.format(campsiteAvailability.getDateFrom()))))
				.andExpect(jsonPath("$.dateTo", is(dateFormat.format(campsiteAvailability.getDateTo()))))
				.andExpect(jsonPath("$.availableDates[0]",
						is(dateFormat.format(campsiteAvailability.getAvailableDates().get(0)))));
	}

}