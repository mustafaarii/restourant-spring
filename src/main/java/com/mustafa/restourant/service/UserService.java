package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.User;

import java.util.List;

public interface UserService {

    void saveUser(User user);
    List<User> allUsers();
    User findByEmail(String email);
}
