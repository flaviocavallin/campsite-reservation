package com.campsite.reservations.controllers.exceptionhandlers;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class DefaultExceptionHandlerUnitTest {

	@Test
	public void defaultExceptionHandlerTest() {
		DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();

		ResponseEntity<Void> responseEntity = defaultExceptionHandler
				.handleDefaultException(new RuntimeException("Test default exception handler"));

		Assertions.assertThat(responseEntity.getBody()).isNull();
		Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}