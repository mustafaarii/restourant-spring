package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.SittingTime;
import com.mustafa.restourant.entity.User;
import com.mustafa.restourant.repository.SittingTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SittingTimeServiceImpl implements SittingTimeService{

    @Autowired
    SittingTimeRepository sittingTimeRepository;

    @Override
    public void saveSittingTime(SittingTime sittingTime) {
        sittingTimeRepository.save(sittingTime);
    }

    @Override
    public SittingTime findByUser(User user) {
        return sittingTimeRepository.findByUser(user);
    }
}
