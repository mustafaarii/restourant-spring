package com.mustafa.restourant.controller;

import com.mustafa.restourant.dto.LoginUserDTO;
import com.mustafa.restourant.dto.UserDTO;
import com.mustafa.restourant.entity.Role;
import com.mustafa.restourant.entity.SittingTime;
import com.mustafa.restourant.entity.Tip;
import com.mustafa.restourant.entity.User;
import com.mustafa.restourant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
public class UserController {

    private final RoleService roleService;
    private final UserService userService;
    private final SittingTimeService sittingTimeService;
    private final TipService tipService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private FilesStorageService filesStorageService;

    public UserController(RoleService roleService, UserService userService, SittingTimeService sittingTimeService, TipService tipService) {
        this.roleService = roleService;
        this.userService = userService;
        this.sittingTimeService = sittingTimeService;
        this.tipService = tipService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<Map> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        response.put("status",false);
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError e : bindingResult.getFieldErrors()) {
                errors.add(e.getDefaultMessage());
            }
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            User isExist = userService.findByEmail(userDTO.getEmail());
            if (isExist != null) {
                response.put("error", "B??yle bir kullan??c?? var.");

            } else {
                Role role = roleService.findByRole("USER");
                User user = new User(userDTO.getEmail(), userDTO.getPassword(), userDTO.getName());
                user.setRole(role);
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userService.saveUser(user);
                sittingTimeService.saveSittingTime(new SittingTime(user));
                response.put("status", true);
                response.put("message", "Kullan??c?? ba??ar??yla olu??turuldu");
            }
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map> createToken(@RequestBody LoginUserDTO authRequest) throws UsernameNotFoundException {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new UsernameNotFoundException("Incorret username or password", ex);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        User user = userService.findByEmail(authRequest.getUsername());
        Map<String,String> response = new HashMap<>();
        response.put("token",jwt);
        response.put("email",user.getEmail());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity getFile(@PathVariable String filename) throws IOException {
        Resource file = filesStorageService.load(filename);
        byte[] bytes = StreamUtils.copyToByteArray(file.getInputStream());
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }


}