package com.mustafa.restourant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class FoodDTO {

    @Size(min = 2,max = 50, message = "Yemek ismi en az 2, en fazla 50 karakter olabilir.")
    @NotEmpty(message = "Yemek ismi alanı boş bırakılamaz.")
    private String foodName;

    @Max(value = 10000,message = "Yemek fiyatı en fazla 10.000 ₺ olabilir.")
    @NotNull(message = "Fiyat alanı boş bırakılamaz.")
    private int price;

    @NotNull(message = "Kategori alanı boş bırakılamaz.")
    private int category;

}
