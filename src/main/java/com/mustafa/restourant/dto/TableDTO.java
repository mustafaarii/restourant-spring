package com.mustafa.restourant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDTO {
    @Size(min = 5, max = 30, message = "Masa ismi en az 5, en fazla 30 karakter olacak şekildedir.")
    @NotEmpty(message = "Masa ismi alanı boş bırakılamaz.")
    private String tableName;
}
