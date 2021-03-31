package com.mustafa.restourant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {

    @NotNull(message = "Başlama zamanı boş olamaz.")
    @Future(message = "Başlama zamanı geçmiş bir tarih olamaz.")
    private Date startTime;

    @NotNull(message = "Bitiş zamanı boş olamaz.")
    @Future(message = "Bitiş zamanı geçmiş bir tarih olamaz.")
    private Date endTime;

    @NotNull(message = "Masa boş bırakılamaz.")
    private Integer tableId;
}
