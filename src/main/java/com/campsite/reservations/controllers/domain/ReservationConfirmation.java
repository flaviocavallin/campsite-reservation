package com.campsite.reservations.controllers.domain;

import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ReservationConfirmation {

	private String campsiteName;
	private String reservationCode;
	private String email;
	private String name;
	private String surname;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date checkinDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date checkoutDate;

	public ReservationConfirmation(String campsiteName, String reservationCode, String email, String name,
			String surname, Date checkinDate, Date checkoutDate) {

		Validate.notBlank(campsiteName, "campsiteName can not be blank");
		Validate.notBlank(reservationCode, "reservationCode can not be blank");
		Validate.notBlank(email, "email can not be blank");
		Validate.notBlank(name, "name can not be blank");
		Validate.notBlank(surname, "surname can not be blank");
		Validate.notNull(checkinDate, "checkinDate can not be blank");
		Validate.notNull(checkoutDate, "checkoutDate can not be blank");

		this.campsiteName = campsiteName;
		this.reservationCode = reservationCode;
		this.email = email;
		this.name = name;
		this.surname = surname;
		this.checkinDate = checkinDate;
		this.checkoutDate = checkoutDate;
	}

	public String getCampsiteName() {
		return campsiteName;
	}

	public String getReservationCode() {
		return reservationCode;
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
