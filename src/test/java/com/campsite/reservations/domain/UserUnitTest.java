package com.campsite.reservations.domain;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class UserUnitTest {

	@Test
	public void userTest() {
		String email = "email@email.com";
		String name = "name1";
		String surname = "surname1";

		User user = new User(email, name, surname);
		Assertions.assertThat(user.getEmail()).isEqualTo(email);
		Assertions.assertThat(user.getName()).isEqualTo(name);
		Assertions.assertThat(user.getSurname()).isEqualTo(surname);
	}
}