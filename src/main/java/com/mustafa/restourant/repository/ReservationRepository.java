package com.mustafa.restourant.repository;

import com.mustafa.restourant.entity.Reservation;
import com.mustafa.restourant.entity.Tables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation,Integer> {

    List<Reservation> findByTableAndStartTimeGreaterThanEqualAndStartTimeLessThanOrderByStartTimeAsc(Tables table,Date start,Date end);

    @Query(value = "SELECT count(id) from reservations r WHERE r.table_id = ?1 AND ((r.start_time>=?2 AND r.start_time<?3) OR (r.end_time>?4 AND r.end_time<=?5))",
    nativeQuery = true)
    int countTotalFullReservation(Tables table,Date sstart, Date send, Date estart, Date eend);
}
