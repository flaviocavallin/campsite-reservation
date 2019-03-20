package com.campsite.reservations.repositories;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.campsite.reservations.domain.User;
import com.campsite.reservations.test.utils.IntegrationBaseTest;

public class UserRepositoryIntegrationTest extends IntegrationBaseTest {

	@Autowired
	private UserRepository userRepository;

	private User user;

	@Before
	public void setUp() {
		this.user = this.userRepository.save(new User("a1@a1.com", "name1", "surname1"));
	}

	@Test
	public void findByEmailTest() {
		Optional<User> opUser = this.userRepository.findByEmail("a1@a1.com");
		Assertions.assertThat(opUser).isPresent();
		Assertions.assertThat(opUser.get()).isEqualTo(user);
	}

}