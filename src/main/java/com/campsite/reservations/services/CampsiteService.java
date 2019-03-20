package com.campsite.reservations.services;

import java.util.Date;

import com.campsite.reservations.controllers.domain.CampsiteAvailability;

public interface CampsiteService {
	void create(String name, int minBookDaysAhead, int maxBookDaysInAdvance, int maxBookNights);

	void createDefaultIfNotExists();

	CampsiteAvailability getAvailability(String campsiteName, Date dateFrom, Date dateTo);
}