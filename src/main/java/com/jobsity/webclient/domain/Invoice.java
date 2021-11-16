package com.jobsity.webclient.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice implements Serializable {

    private Long id;
    private LocalDate issued;
    private BigDecimal total;

}
