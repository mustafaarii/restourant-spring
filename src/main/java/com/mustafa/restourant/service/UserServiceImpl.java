package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.User;
import com.mustafa.restourant.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> allUsers() { return userRepository.findAll(); }

    @Override
    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
