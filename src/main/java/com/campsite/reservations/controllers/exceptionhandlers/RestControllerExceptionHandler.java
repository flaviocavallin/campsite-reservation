package com.campsite.reservations.controllers.exceptionhandlers;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.campsite.reservations.exceptions.CampsiteAlreadyReservedException;
import com.campsite.reservations.exceptions.ReservationIllegalArgumentException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestControllerExceptionHandler.class);

	@ExceptionHandler({ CampsiteAlreadyReservedException.class, ReservationIllegalArgumentException.class })
	public ResponseEntity<ApiError> handleCampsiteAlreadyReservedException(RuntimeException ex) {
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, new Date(), ex.getMessage());

		LOGGER.debug("Exception handled={}", apiError);

		return ResponseEntity.badRequest().body(apiError);
	}

}