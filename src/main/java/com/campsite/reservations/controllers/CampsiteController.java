package com.campsite.reservations.controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campsite.reservations.controllers.domain.CampsiteAvailability;
import com.campsite.reservations.services.CampsiteService;

@RestController
@RequestMapping(value = "/campsites", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class CampsiteController {

	private CampsiteService campsiteService;
	private String defaultCampsiteName;

	CampsiteController(CampsiteService campsiteService, @Value("${default.campsite.name}") String defaultCampsiteName) {
		this.campsiteService = Objects.requireNonNull(campsiteService, "campsiteService can not be null");
		this.defaultCampsiteName = Objects.requireNonNull(defaultCampsiteName, "defaultCampsiteName can not be null");
	}

	@GetMapping("/availability")
	public ResponseEntity<CampsiteAvailability> getAvailability(
			@RequestParam(value = "campsiteName", required = false) String campsiteName,
			@RequestParam(value = "dateFrom", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date requiredDateFrom,
			@RequestParam(value = "dateTo", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date requiredDateTo) {

		String name = StringUtils.defaultIfBlank(campsiteName, defaultCampsiteName);

		Date dateFrom = DateUtils.truncate(new Date(), Calendar.DATE);
		if (requiredDateFrom != null) {
			dateFrom = requiredDateFrom;
		}

		Date dateTo = DateUtils.addMonths(dateFrom, 1);
		if (requiredDateTo != null) {
			dateTo = requiredDateTo;
		}

		CampsiteAvailability availability = this.campsiteService.getAvailability(name, dateFrom, dateTo);

		return ResponseEntity.ok(availability);
	}

}