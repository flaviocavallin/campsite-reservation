package com.campsite.reservations.domain;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.Persistable;

/**
 * This entity is used as LOCK table to register the reserved dates by campsite.
 * This LOCK entity will be purged with a daily cron process. Has not relation
 * with the entity Reservation because on high TPS we want to avoid as much as
 * possible DB locking on RESERVATION table.
 * 
 * @author Flavio
 *
 */
@Entity
@Table(name = "CAMPSITE_RESERVED_DATES")
@Cacheable
public class CampsiteReservedDate extends AbstractEntity<CampsiteReservedDateId>
		implements Persistable<CampsiteReservedDateId> {

	private static final long serialVersionUID = 6130076934175605842L;

	@EmbeddedId
	private CampsiteReservedDateId campsiteReservedDateId;

	CampsiteReservedDate() {
		// do nothing
	}

	public CampsiteReservedDate(Campsite campsite, Date dateReserved) {
		this();
		Validate.notNull(campsite, "campsite can not be null");
		Validate.notNull(dateReserved, "dateReserved can not be null");

		this.campsiteReservedDateId = new CampsiteReservedDateId(campsite, dateReserved);
		campsite.addCampsiteReservedDates(this);
	}

	@Override
	public CampsiteReservedDateId getId() {
		return campsiteReservedDateId;
	}

	@Override
	public boolean isNew() {
		return true;
	}
}