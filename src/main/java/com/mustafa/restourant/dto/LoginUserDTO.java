package com.mustafa.restourant.dto;

import lombok.Data;

@Data
public class LoginUserDTO {

    private String username;
    private String password;

    public LoginUserDTO() {

    }

    public LoginUserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }



}
