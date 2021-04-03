package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Reservation;
import com.mustafa.restourant.entity.Tables;
import com.mustafa.restourant.entity.User;

import java.util.Date;
import java.util.List;

public interface ReservationService {

    List<Reservation> findAll();
    void saveReservation(Reservation reservation);
    int hoveManyReservations(Date start, Date end,Tables table);
    List<Reservation> findReservationByDate(Tables table,Date start, Date end);
    Reservation findFirstReservationByTable(Tables table,Date date);
    int countReservationByStartTimeAndTable(Date start,Date end,Tables table);
    Reservation findReservationNow(Date date, User user);
    void deleteReservationById(int id);
}
