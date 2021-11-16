package com.schambeck.webclient.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Invoice implements Serializable {

    private Long id;
    private LocalDate issued;
    private BigDecimal total;

}
