package com.mustafa.restourant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDTO {
    private int foodId;
    private int count;

}
