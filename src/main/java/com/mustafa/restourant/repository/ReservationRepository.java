package com.mustafa.restourant.repository;

import com.mustafa.restourant.entity.Reservation;
import com.mustafa.restourant.entity.Tables;
import com.mustafa.restourant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation,Integer> {

    List<Reservation> findByTableAndStartTimeGreaterThanEqualAndStartTimeLessThanOrderByStartTimeAsc(Tables table,Date start,Date end);
    Reservation findFirstByTableAndStartTimeGreaterThanEqualOrderByStartTimeAsc(Tables table,Date date);

    @Query(value = "SELECT count(id) from reservations r WHERE r.table_id = ?1 AND ((r.start_time>=?2 AND r.start_time<?3) OR (r.end_time>?4 AND r.end_time<=?5))",
    nativeQuery = true)
    int countTotalFullReservation(Tables table,Date sstart, Date send, Date estart, Date eend);

    int countByStartTimeBetweenAndTable(Date start,Date end,Tables table);

    @Query(value = "SELECT r FROM Reservation r WHERE r.startTime<=?1 AND r.endTime>?1 and r.user = ?2")
    Reservation findReservationNow(Date date, User user);

    void deleteById(int id);
}
