package com.campsite.reservations.controllers.exceptionhandlers;

import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.http.HttpStatus;

public class ApiErrorUnitTest {

	@Test
	public void apiErrorTest() {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Date date = new Date();
		String message = "message";

		ApiError apiError = new ApiError(status, date, message);

		Assertions.assertThat(apiError.getStatus()).isEqualTo(status);
		Assertions.assertThat(apiError.getDate()).isEqualTo(date);
		Assertions.assertThat(apiError.getMessage()).isEqualTo(message);
	}
}
