package com.campsite.reservations.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateUtils;

@Embeddable
public class CampsiteReservedDateId implements Serializable {

	private static final long serialVersionUID = 3524688583848350201L;

	@JoinColumn(name = "campsiteId", nullable = false)
	@ManyToOne(optional = false)
	private Campsite campsite;

	@Column(name = "date_reserved", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dateReserved;

	CampsiteReservedDateId() {
		// do nothing
	}

	public CampsiteReservedDateId(Campsite campsite, Date dateReserved) {
		this();
		Validate.notNull(campsite, "campsite can not be null");
		Validate.notNull(dateReserved, "dateReserved can not be null");

		this.campsite = campsite;
		this.dateReserved = DateUtils.truncate(dateReserved, Calendar.DATE);
	}

	public Campsite getCampsite() {
		return campsite;
	}

	public Date getDateReserved() {
		return dateReserved;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((campsite == null) ? 0 : campsite.hashCode());
		result = prime * result + ((dateReserved == null) ? 0 : dateReserved.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CampsiteReservedDateId other = (CampsiteReservedDateId) obj;
		if (campsite == null) {
			if (other.campsite != null)
				return false;
		} else if (!campsite.equals(other.campsite))
			return false;
		if (dateReserved == null) {
			if (other.dateReserved != null)
				return false;
		} else if (!dateReserved.equals(other.dateReserved))
			return false;
		return true;
	}

}