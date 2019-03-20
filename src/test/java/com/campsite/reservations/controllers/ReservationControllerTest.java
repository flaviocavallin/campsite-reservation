package com.campsite.reservations.controllers;

import com.campsite.reservations.controllers.domain.ReservationConfirmation;
import com.campsite.reservations.controllers.domain.ReservationDetails;
import com.campsite.reservations.exceptions.CampsiteAlreadyReservedException;
import com.campsite.reservations.exceptions.ReservationIllegalArgumentException;
import com.campsite.reservations.services.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ReservationController.class)
@ActiveProfiles("test")
public class ReservationControllerTest {

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${default.campsite.name}")
    private String defaultCampsiteName;

    private FastDateFormat dateFormat = FastDateFormat.getInstance("dd-MM-yyyy");

    @Test
    public void reserveTest() throws Exception {
        String campsiteName = defaultCampsiteName;
        String email = "a1@a1.com";
        String name = "name1";
        String surname = "surname1";
        String reservationCode = "code1";

        Date checkinDate = DateUtils.truncate(new Date(), Calendar.DATE);
        Date checkoutDate = DateUtils.addDays(checkinDate, 1);

        ReservationDetails reservationDetails = new ReservationDetails(campsiteName, email, name, surname, checkinDate,
                checkoutDate);

        String jsonReservationDetails = objectMapper.writeValueAsString(reservationDetails);

        ReservationConfirmation reservationConfirmation = new ReservationConfirmation(campsiteName, reservationCode,
                email, name, surname, checkinDate, checkoutDate);

        given(reservationService.reserve(campsiteName, email, name, surname, checkinDate, checkoutDate))
                .willReturn(reservationConfirmation);

        mvc.perform(post("/reservations/reserve").content(jsonReservationDetails)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.reservationCode", is(reservationCode)))
                .andExpect(jsonPath("$.campsiteName", is(campsiteName))).andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.surname", is(surname)))
                .andExpect(jsonPath("$.checkinDate", is(dateFormat.format(checkinDate))))
                .andExpect(jsonPath("$.checkoutDate", is(dateFormat.format(checkoutDate))));
    }

    @Test
    public void modifyTest() throws Exception {
        String campsiteName = defaultCampsiteName;
        String email = "a1@a1.com";
        String name = "name1";
        String surname = "surname1";
        String reservationCode = "code1";

        Date checkinDate = DateUtils.truncate(new Date(), Calendar.DATE);
        Date checkoutDate = DateUtils.addDays(checkinDate, 1);

        ReservationConfirmation reservationConfirmation = new ReservationConfirmation(campsiteName, reservationCode,
                email, name, surname, checkinDate, checkoutDate);

        given(reservationService.modify(reservationCode, checkinDate, checkoutDate))
                .willReturn(reservationConfirmation);

        mvc.perform(put("/reservations/{reservationCode}/modify", reservationCode)
                .param("checkinDate", dateFormat.format(checkinDate))
                .param("checkoutDate", dateFormat.format(checkoutDate)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.reservationCode", is(reservationCode)))
                .andExpect(jsonPath("$.campsiteName", is(campsiteName))).andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.surname", is(surname)))
                .andExpect(jsonPath("$.checkinDate", is(dateFormat.format(checkinDate))))
                .andExpect(jsonPath("$.checkoutDate", is(dateFormat.format(checkoutDate))));
    }

    @Test
    public void cancelTest() throws Exception {
        String reservationCode = "code1";

        mvc.perform(delete("/reservations/{reservationCode}/cancel", reservationCode)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());

        Mockito.verify(reservationService).cancelReservation(reservationCode);
    }

    @Test
    public void reserveOverlappingDatesTest() throws Exception {
        String errorMessage = "Exist one reservation for the dates";

        String campsiteName = defaultCampsiteName;
        String email = "a1@a1.com";
        String name = "name1";
        String surname = "surname1";

        Date checkinDate = DateUtils.truncate(new Date(), Calendar.DATE);
        Date checkoutDate = DateUtils.addDays(checkinDate, 1);

        ReservationDetails reservationDetails = new ReservationDetails(campsiteName, email, name, surname, checkinDate,
                checkoutDate);

        String jsonReservationDetails = objectMapper.writeValueAsString(reservationDetails);

        given(reservationService.reserve(campsiteName, email, name, surname, checkinDate, checkoutDate))
                .willThrow(new CampsiteAlreadyReservedException(errorMessage));

        mvc.perform(post("/reservations/reserve").content(jsonReservationDetails)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    public void reservationCodeNotExistTest() throws Exception {
        String reservationCode = "code1";
        String message = "Reservation code not exists";

        Mockito.doThrow(new ReservationIllegalArgumentException(message)).when(reservationService)
                .cancelReservation(reservationCode);

        mvc.perform(delete("/reservations/{reservationCode}/cancel", reservationCode)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.message", is(message)));

        Mockito.verify(reservationService).cancelReservation(reservationCode);
    }

}