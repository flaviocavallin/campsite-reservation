package com.campsite.reservations.services;

import java.util.Date;

import com.campsite.reservations.domain.Campsite;
import com.campsite.reservations.domain.Reservation;

public interface CampsiteReservedDateService {
	void reserveCampsite(Reservation reservation);

	void removeByCampsiteAndDateRange(Campsite campsite, Date dateFrom, Date dateTo);

}