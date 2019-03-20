package com.campsite.reservations.test.utils;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.campsite.reservations.ReservationsApplication;

@RunWith(SpringRunner.class)
@Transactional
@Rollback
@ActiveProfiles("test")
@SpringBootTest(classes = ReservationsApplication.class)
public abstract class IntegrationBaseTest {
	// do nothing
}
