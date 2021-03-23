package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Role;
import com.mustafa.restourant.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User exist = userService.findByEmail(s);
        if (exist == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", s));
        } else {
            UserDetails user = org.springframework.security.core.userdetails.User.withUsername(exist.getEmail()).password(exist.getPassword()).authorities(getAuthority(exist)).build();
            return user;
        }
    }

    private Set getAuthority(User user) {
        Set authorities = new HashSet<>();
        Role role = user.getRole();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRole()));
        return authorities;
    }
}
