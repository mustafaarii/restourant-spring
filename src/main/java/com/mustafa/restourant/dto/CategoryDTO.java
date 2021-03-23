package com.mustafa.restourant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @NotEmpty(message = "Kategori ismi alanı boş bırakılamaz.")
    @Size(min = 3,max = 255,message = "Kategori ismi en az 3, en fazla 255 karakter olabilir.")
    private String name;
}
