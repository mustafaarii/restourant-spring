package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Reservation;
import com.mustafa.restourant.entity.Tables;
import com.mustafa.restourant.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService{

    @Autowired
    ReservationRepository reservationRepository;

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public void saveReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    @Override
    public int hoveManyReservations(Date start, Date end, Tables table) {
      return reservationRepository.countTotalFullReservation(table,start,end,start,end);
    }

    @Override
    public List<Reservation> findReservationByDate(Tables table,Date start,Date end) {
        return reservationRepository.findByTableAndStartTimeGreaterThanEqualAndStartTimeLessThanOrderByStartTimeAsc(table,start,end);
    }
}
