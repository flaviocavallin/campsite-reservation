package com.campsite.reservations.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.Validate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "USERS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends AbstractEntity<Integer> {

	private static final long serialVersionUID = -8990226958452772812L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "EMAIL", unique = true)
	private String email;

	@Column(name = "NAME")
	private String name;

	@Column(name = "SURNAME")
	private String surname;

	User() {
		// do nothing for hydration purpose
	}

	public User(String email, String name, String surname) {
		this();
		Validate.notBlank(email, "email can not be blank");
		Validate.notBlank(name, "name can not be blank");
		Validate.notBlank(surname, "surname can not be blank");

		this.email = email;
		this.name = name;
		this.surname = surname;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

}
