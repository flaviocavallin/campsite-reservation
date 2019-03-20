package com.campsite.reservations.domain;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.Validate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "CAMPSITES")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Campsite extends AbstractEntity<Integer> {

	private static final long serialVersionUID = 1591251006314856671L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "NAME", unique = true, nullable = false)
	private String name;

	@Column(name = "MIN_BOOK_DAYS_AHEAD", nullable = false)
	private int minBookDaysAhead;

	@Column(name = "MAX_BOOK_DAYS_ADVANCE", nullable = false)
	private int maxBookDaysInAdvance;

	@Column(name = "MAX_BOOK_NIGHTS", nullable = false)
	private int maxBookNights;

	@OneToMany(mappedBy = "campsiteReservedDateId.campsite", fetch = FetchType.LAZY)
	private List<CampsiteReservedDate> campsiteReservedDates = new LinkedList<>();

	Campsite() {
		// do nothing for hydration purpose
	}

	public Campsite(String name, int minBookDaysAhead, int maxBookDaysInAdvance, int maxBookNights) {
		this();
		Validate.notBlank(name, "name can not be blank");
		Validate.isTrue(minBookDaysAhead > 0, "The value must be greater than zero. minBookDaysAhead=%d",
				minBookDaysAhead);
		Validate.isTrue(maxBookDaysInAdvance > 0, "The value must be greater than zero. maxBookDaysInAdvance=%d",
				maxBookDaysInAdvance);
		Validate.isTrue(maxBookNights > 0, "The value must be greater than zero. maxBookNights=%d", maxBookNights);

		this.name = name;
		this.minBookDaysAhead = minBookDaysAhead;
		this.maxBookDaysInAdvance = maxBookDaysInAdvance;
		this.maxBookNights = maxBookNights;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}

	public int getMaxBookNights() {
		return maxBookNights;
	}

	public int getMinBookDaysAhead() {
		return minBookDaysAhead;
	}

	public int getMaxBookDaysInAdvance() {
		return maxBookDaysInAdvance;
	}

	public void addCampsiteReservedDates(CampsiteReservedDate campsiteReservedDate) {
		Validate.notNull(campsiteReservedDate, "campsiteReservedDate can not be null");
		this.campsiteReservedDates.add(campsiteReservedDate);
	}

	public List<CampsiteReservedDate> getCampsiteReservedDates() {
		return campsiteReservedDates;
	}
}