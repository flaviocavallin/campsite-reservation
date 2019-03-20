package com.campsite.reservations.controllers.domain;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ReservationDetails {

	private String campsiteName;
	private String email;
	private String name;
	private String surname;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date checkinDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date checkoutDate;

	ReservationDetails() {

	}

	public ReservationDetails(String campsiteName, String email, String name, String surname, Date checkinDate,
			Date checkoutDate) {
		this();
		this.campsiteName = Objects.requireNonNull(campsiteName, "campsiteName can not be null");
		this.email = Objects.requireNonNull(email, "email can not be null");
		this.name = Objects.requireNonNull(name, "name can not be null");
		this.surname = Objects.requireNonNull(surname, "surname can not be null");
		this.checkinDate = Objects.requireNonNull(checkinDate, "checkinDate can not be null");
		this.checkoutDate = Objects.requireNonNull(checkoutDate, "checkoutDate can not be null");
	}

	public String getCampsiteName() {
		return campsiteName;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public Date getCheckinDate() {
		return checkinDate;
	}

	public Date getCheckoutDate() {
		return checkoutDate;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
