package com.mustafa.restourant.repository;

import com.mustafa.restourant.entity.SittingTime;
import com.mustafa.restourant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SittingTimeRepository extends JpaRepository<SittingTime,Integer> {
    SittingTime findByUser(User user);
}
