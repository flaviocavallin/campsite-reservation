package com.campsite.reservations.controllers;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campsite.reservations.controllers.domain.ReservationConfirmation;
import com.campsite.reservations.controllers.domain.ReservationDetails;
import com.campsite.reservations.services.ReservationService;

@RestController
@RequestMapping(value = "/reservations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ReservationController {

	private ReservationService reservationService;
	private String defaultCampsiteName;

	ReservationController(ReservationService reservationService,
			@Value("${default.campsite.name}") String defaultCampsiteName) {
		this.reservationService = Objects.requireNonNull(reservationService, "reservationService can not be null");
		this.defaultCampsiteName = Objects.requireNonNull(defaultCampsiteName, "defaultCampsiteName can not be null");
	}

	@PostMapping("/reserve")
	public ResponseEntity<ReservationConfirmation> reserve(@RequestBody ReservationDetails reservationDetails) {
		String campsiteName = StringUtils.defaultIfBlank(reservationDetails.getCampsiteName(), defaultCampsiteName);

		ReservationConfirmation reservationConfirmation = this.reservationService.reserve(campsiteName,
				reservationDetails.getEmail(), reservationDetails.getName(), reservationDetails.getSurname(),
				reservationDetails.getCheckinDate(), reservationDetails.getCheckoutDate());

		return ResponseEntity.ok(reservationConfirmation);
	}

	@PutMapping("/{reservationCode}/modify")
	public ResponseEntity<ReservationConfirmation> modify(
			@PathVariable(value = "reservationCode") String reservationCode,
			@RequestParam(value = "checkinDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date checkinDate,
			@RequestParam(value = "checkoutDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date checkoutDate) {

		ReservationConfirmation reservationConfirmation = this.reservationService.modify(reservationCode, checkinDate,
				checkoutDate);

		return ResponseEntity.ok(reservationConfirmation);
	}

	@DeleteMapping("/{reservationCode}/cancel")
	public void cancel(@PathVariable(value = "reservationCode") String reservationCode) {
		this.reservationService.cancelReservation(reservationCode);
	}
}
