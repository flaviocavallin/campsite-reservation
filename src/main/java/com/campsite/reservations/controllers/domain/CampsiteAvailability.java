package com.campsite.reservations.controllers.domain;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CampsiteAvailability {

	private String campsiteName;

	@JsonFormat(pattern = "dd-MM-yyyy")
	private Date dateFrom;

	@JsonFormat(pattern = "dd-MM-yyyy")
	private Date dateTo;

	@JsonFormat(pattern = "dd-MM-yyyy")
	private List<Date> availableDates = new LinkedList<>();

	public CampsiteAvailability(String campsiteName, Date dateFrom, Date dateTo) {
		this.campsiteName = Objects.requireNonNull(campsiteName, "campsiteName can not be null");
		this.dateFrom = Objects.requireNonNull(dateFrom, "dateFrom can not be null");
		this.dateTo = Objects.requireNonNull(dateTo, "dateTo can not be null");
	}

	public void addAvailableDate(Date availableDate) {
		Validate.notNull(availableDate, "availableDate can not be null");
		this.availableDates.add(availableDate);
	}

	public String getCampsiteName() {
		return campsiteName;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public List<Date> getAvailableDates() {
		return availableDates;
	}

}
