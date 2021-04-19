package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.SittingTime;
import com.mustafa.restourant.entity.User;

public interface SittingTimeService {
    void saveSittingTime(SittingTime sittingTime);
    SittingTime findByUser(User user);
}
