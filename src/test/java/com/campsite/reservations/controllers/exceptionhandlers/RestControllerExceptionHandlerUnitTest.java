package com.campsite.reservations.controllers.exceptionhandlers;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.campsite.reservations.exceptions.CampsiteAlreadyReservedException;

public class RestControllerExceptionHandlerUnitTest {

	private static final String ERROR_MESSAGE = "Test default exception handler";

	@Test
	public void restControllerExceptionHandlerTest() {
		RestControllerExceptionHandler handler = new RestControllerExceptionHandler();

		ResponseEntity<ApiError> responseEntity = handler
				.handleCampsiteAlreadyReservedException(new CampsiteAlreadyReservedException(ERROR_MESSAGE));

		Assertions.assertThat(responseEntity.getBody()).isNotNull();
		Assertions.assertThat(responseEntity.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
		Assertions.assertThat(responseEntity.getBody().getMessage()).isEqualTo(ERROR_MESSAGE);
		Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
}