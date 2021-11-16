package com.schambeck.webclient.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Invoice implements Serializable {

    @EqualsAndHashCode.Include
    private Long id;
    private LocalDate issued;
    private BigDecimal total;

}
