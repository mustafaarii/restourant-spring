package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Reservation;
import com.mustafa.restourant.entity.Tables;
import com.mustafa.restourant.entity.User;
import com.mustafa.restourant.exception.NotFoundException;
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

    @Override
    public Reservation findFirstReservationByTable(Tables table,Date date) {
        return reservationRepository.findFirstByTableAndStartTimeGreaterThanEqualOrderByStartTimeAsc(table,date);
    }

    @Override
    public int countReservationByStartTimeAndTable(Date start, Date end, Tables table) {
        return reservationRepository.countByStartTimeBetweenAndTable(start,end,table);
    }

    @Override
    public Reservation findReservationNow(Date date, User user) {
        return reservationRepository.findReservationNow(date,user);
    }

    @Override
    public void deleteReservationById(int id) {
        try{
            reservationRepository.deleteById(id);
        }catch (Exception e){
            throw new NotFoundException();
        }
    }

    @Override
    public void deleteOldReservations(User user){
        Date date = new Date();
        reservationRepository.deleteByEndTimeBeforeAndUser(date,user);
    }

    @Override
    public Reservation findById(int id) {
        return reservationRepository.findById(id).get();
    }

    @Override
    public int countUserReservations(User user) {
        Date date = new Date();
        return reservationRepository.countReservationByUserAndEndTimeAfter(user,date);
    }
}
