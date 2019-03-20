package com.campsite.reservations.exceptions;

public class ReservationIllegalArgumentException extends RuntimeException {

	private static final long serialVersionUID = 6576774821728668862L;

	public ReservationIllegalArgumentException(String message) {
		super(message);
	}

}