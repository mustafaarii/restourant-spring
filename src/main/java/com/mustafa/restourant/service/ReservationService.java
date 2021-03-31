package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Reservation;
import com.mustafa.restourant.entity.Tables;

import java.util.Date;
import java.util.List;

public interface ReservationService {

    List<Reservation> findAll();
    void saveReservation(Reservation reservation);
    int hoveManyReservations(Date start, Date end,Tables table);
    List<Reservation> findReservationByDate(Tables table,Date start, Date end);
}
