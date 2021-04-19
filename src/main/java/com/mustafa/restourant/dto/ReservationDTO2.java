package com.mustafa.restourant.dto;

import com.mustafa.restourant.entity.Tables;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO2 {
    private int id;
    private Date startTime;
    private Date endTime;
    private Tables table;
}
