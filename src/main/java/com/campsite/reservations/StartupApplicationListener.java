package com.campsite.reservations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.campsite.reservations.services.CampsiteService;

/**
 * Populates with default data if not exists on startup
 * 
 * @author Flavio
 *
 */

@Component
@Profile("!test")
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private CampsiteService campsiteService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		campsiteService.createDefaultIfNotExists();
	}

}
