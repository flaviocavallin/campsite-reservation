package com.campsite.reservations.exceptions;

/**
 * Thrown to indicate that the campsite has been reserved
 * 
 * @author Flavio
 *
 */
public class CampsiteAlreadyReservedException extends RuntimeException {

	private static final long serialVersionUID = 2979233991954527043L;

	public CampsiteAlreadyReservedException(String message) {
		super(message);
	}
}