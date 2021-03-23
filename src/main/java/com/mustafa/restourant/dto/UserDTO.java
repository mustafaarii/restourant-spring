package com.mustafa.restourant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @Email(message = "Lütfen bir email giriniz.")
    @NotEmpty(message = "Email alanı boş bırakılamaz.")
    private String email;

    @NotEmpty(message = "Şifre alanı boş bırakılamaz.")
    @Size(min = 8, message = "Şifre en az 8 karakter olmalıdır.")
    private String password;

    @NotEmpty(message = "İsim alanı boş bırakılamaz.")
    @Size(min = 2, message = "İsim alanı en az 2 karakter olmalıdır.")
    private String name;

}
